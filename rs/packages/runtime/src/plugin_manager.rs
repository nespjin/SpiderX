// Copyright (c) 2025. NESP Technology Corporation.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

use core::data::plugin::Plugin;
use std::{
    fs,
    path::Path,
    sync::{Mutex, OnceLock},
};

use compiler::{json_plugin_compiler::JsonPluginCompiler, plugin_compiler::PluginCompiler};

use crate::{
    cache::CacheManager,
    database::connection_pool::DatabasePool,
    executor::{
        request_dataset_listener::RequestDatasetListenerWrpper,
        request_javascript_dataset_config::RequestJavaScriptDatasetConfigArc,
    },
    repository::{dataset_repository::DatasetRepository, plugin_repository::PluginRepository},
};

#[derive(Debug, Clone)]
pub struct PluginManagerConfig {
    pub database_path: String,
}

pub enum PluginSource {
    ManifestJson(String),
    ManifestJsonFile(String),
}

pub struct PluginManager {
    config: Option<PluginManagerConfig>,
    dataset_repository: Option<DatasetRepository>,
    plugin_repository: Option<PluginRepository>,
    request_javascript_dataset_config: Option<RequestJavaScriptDatasetConfigArc>,
}

impl PluginManager {
    pub fn get_instance() -> &'static Mutex<PluginManager> {
        static INSTANCE: OnceLock<Mutex<PluginManager>> = OnceLock::new();
        INSTANCE.get_or_init(|| {
            Mutex::new(PluginManager {
                config: None,
                dataset_repository: None,
                plugin_repository: None,
                request_javascript_dataset_config: None,
            })
        })
    }

    pub fn init(&mut self, config: PluginManagerConfig) -> Result<(), String> {
        if self.config.is_some() {
            return Err("PluginManager has been initialized.".to_string());
        }

        let database_path_str = &config.database_path;
        let database_path = Path::new(database_path_str);

        // Create parent directory if it doesn't exist
        if let Some(parent) = database_path.parent() {
            if !parent.exists() {
                fs::create_dir_all(parent).map_err(|e| e.to_string())?;
            }
        }

        // Initialize connection pool
        DatabasePool::init(database_path_str)?;
        log::info!("Connection pool initialized for: {}", database_path_str);

        // Initialize cache manager
        CacheManager::init();
        log::info!("Cache manager initialized");

        self.plugin_repository = Some(PluginRepository::new());
        self.dataset_repository = Some(DatasetRepository::new());

        // Run migrations using pooled connection
        let pool = DatabasePool::get_instance()
            .get()
            .ok_or("Failed to get database pool instance")?;
        pool.run_migrations()?;

        self.config = Some(config);

        log::info!("PluginManager initialized successfully with connection pooling and caching");
        Ok(())
    }

    pub fn set_request_javascript_dataset_config(
        &mut self,
        request_javascript_dataset_config: RequestJavaScriptDatasetConfigArc,
    ) {
        self.request_javascript_dataset_config = Some(request_javascript_dataset_config);
    }

    pub fn get_request_javascript_dataset_config(
        &self,
    ) -> Option<RequestJavaScriptDatasetConfigArc> {
        self.request_javascript_dataset_config.clone()
    }

    pub fn install_plugin(&self, source: &PluginSource) -> Result<(), String> {
        match source {
            PluginSource::ManifestJson(json) => self.install_plugin_from_manifest_json(json),
            PluginSource::ManifestJsonFile(path) => {
                let json = fs::read_to_string(path)
                    .map_err(|err| format!("Failed to read plugin manifest file: {}", err))?;
                self.install_plugin_from_manifest_json(&json)
            }
        }
    }

    fn install_plugin_from_manifest_json(&self, json: &str) -> Result<(), String> {
        let compiler = JsonPluginCompiler::parse(json).map_err(|e| e.to_string())?;
        self.do_install_plugin(compiler.compile()?)?;
        Ok(())
    }

    fn do_install_plugin(&self, plugin: Plugin) -> Result<(), String> {
        self.ensure_initialized()?;
        let plugin_id = plugin.id.to_string();
        let datasets = plugin.datasets.clone();

        self.plugin_repository
            .as_ref()
            .ok_or("PluginRepository is not initialized".to_string())?
            .save_plugin(plugin)?;

        self.dataset_repository
            .as_ref()
            .ok_or("DatasetRepository is not initialized".to_string())?
            .save_datasets(&plugin_id, datasets)?;

        Ok(())
    }

    pub fn is_plugin_installed(&self, id: &str) -> Result<bool, String> {
        self.ensure_initialized()?;
        self.plugin_repository
            .as_ref()
            .ok_or("PluginRepository is not initialized".to_string())?
            .is_plugin_exists(id)
    }

    pub fn get_installed_plugin(&self, id: &str) -> Result<Option<Plugin>, String> {
        self.ensure_initialized()?;
        self.plugin_repository
            .as_ref()
            .ok_or("PluginRepository is not initialized".to_string())?
            .get_plugin(id)
    }

    pub fn get_installed_plugins(&self) -> Result<Vec<Plugin>, String> {
        self.ensure_initialized()?;
        self.plugin_repository
            .as_ref()
            .ok_or("PluginRepository is not initialized".to_string())?
            .get_plugins()
    }

    /// Uninstall a plugin by id
    pub fn uninstall_plugin(&mut self, id: &str) -> Result<(), String> {
        self.ensure_initialized()?;

        self.plugin_repository
            .as_ref()
            .ok_or("PluginRepository is not initialized".to_string())?
            .delete_plugin(id)?;

        self.dataset_repository
            .as_ref()
            .ok_or("DatasetRepository is not initialized".to_string())?
            .delete_datasets(id)?;

        Ok(())
    }

    pub fn request_dataset(
        &self,
        plugin_id: &str,
        dataset_id: &str,
        r#type: RequestType,
        listener: Option<RequestDatasetListenerWrpper>,
    ) -> Result<String, String> {
        self.ensure_initialized()?;
        self.dataset_repository
            .as_ref()
            .ok_or("DatasetRepository is not initialized".to_string())?
            .request_dataset(
                plugin_id,
                dataset_id,
                r#type,
                listener,
                self.request_javascript_dataset_config.clone(),
            )
    }

    pub fn auto_request_type(
        &self,
        plugin_id: &str,
        dataset_id: &str,
    ) -> Result<RequestType, String> {
        self.ensure_initialized()?;
        self.dataset_repository
            .as_ref()
            .ok_or("DatasetRepository is not initialized".to_string())?
            .auto_request_type(plugin_id, dataset_id)
    }

    fn ensure_initialized(&self) -> Result<(), String> {
        if self.config.is_none() {
            return Err("The plugin manager is not initialized".to_string());
        }
        Ok(())
    }
}

pub enum RequestType {
    Auto = 0,
    JavaScript,
    Dsl,
}

impl TryFrom<i32> for RequestType {
    type Error = ();

    fn try_from(value: i32) -> Result<Self, Self::Error> {
        match value {
            x if x == RequestType::Auto as i32 => Ok(RequestType::Auto),
            x if x == RequestType::JavaScript as i32 => Ok(RequestType::JavaScript),
            x if x == RequestType::Dsl as i32 => Ok(RequestType::Dsl),
            _ => Err(()),
        }
    }
}

#[cfg(test)]
mod test {
    use super::*;

    #[test]
    fn test_plugin_manager_init() {
        let plugin_manager = PluginManager::get_instance();
        let mut plugin_manager = plugin_manager.lock().unwrap();
        let config = PluginManagerConfig {
            database_path: "target/runtime.db".to_string(),
        };
        let result = plugin_manager.init(config.clone());
        assert!(result.is_ok());
        let result = plugin_manager.init(config.clone());
        assert!(result.is_err());
    }
}
