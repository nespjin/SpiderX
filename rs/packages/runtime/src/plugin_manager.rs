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
    sync::{Arc, Mutex, OnceLock},
};

use compiler::{json_plugin_compiler::JsonPluginCompiler, plugin_compiler::PluginCompiler};

use crate::{
    database::database,
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
}

impl PluginManager {
    pub fn get_instance() -> Arc<Mutex<PluginManager>> {
        static INSTANCE: OnceLock<Arc<Mutex<PluginManager>>> = OnceLock::new();
        INSTANCE
            .get_or_init(|| {
                Arc::new(Mutex::new(PluginManager {
                    config: None,
                    dataset_repository: None,
                    plugin_repository: None,
                }))
            })
            .clone()
    }

    pub fn init(&mut self, config: PluginManagerConfig) -> Result<(), String> {
        if self.config.is_some() {
            return Err("PluginManager has been initialized.".to_string());
        }

        let database_path_str = config.database_path.clone();
        let database_path = Path::new(&database_path_str);
        let database_parent_path = database_path.parent();
        let is_database_parent_path_exists =
            &database_path.parent().map(|e| e.exists()).unwrap_or(true);
        if !is_database_parent_path_exists {
            fs::create_dir_all(&database_parent_path.unwrap()).map_err(|e| e.to_string())?;
        }

        self.plugin_repository = Some(PluginRepository::new(database_path_str.clone()));
        self.dataset_repository = Some(DatasetRepository::new(database_path_str.clone()));

        // Initialize the database.
        let mut conn: diesel::SqliteConnection =
            database::open(&database_path_str.clone()).map_err(|e| e.to_string())?;
        database::run_migrations(&mut conn).map_err(|e| e.to_string())?;

        self.config = Some(config);

        Ok(())
    }

    pub fn install_plugin(&self, source: &PluginSource) -> Result<(), String> {
        match source {
            PluginSource::ManifestJson(json) => self.install_plugin_from_manifest_json(json),
            PluginSource::ManifestJsonFile(path) => fs::read_to_string(path)
                .map_err(|err| format!("Failed to read plugin manifest file: {}", err))
                .and_then(|json| self.install_plugin_from_manifest_json(&json)),
        }
    }

    fn install_plugin_from_manifest_json(&self, json: &String) -> Result<(), String> {
        let compiler = JsonPluginCompiler::parse(json).map_err(|e| e.to_string())?;
        self.do_install_plugin(compiler.compile()?)?;
        Ok(())
    }

    fn do_install_plugin(&self, plugin: Plugin) -> Result<(), String> {
        self.ensure_initialized()?;
        let plugin_id = &plugin.id.to_string();
        let datasets = plugin.datasets.clone();
        match self.plugin_repository.as_ref() {
            Some(repo) => repo.save_plugin(plugin),
            None => Err("PluginRepository is not initialized".to_string()),
        }?;
        match self.dataset_repository.as_ref() {
            Some(repo) => repo.save_datasets(plugin_id, datasets),
            None => Err("DatasetRepository is not initialized".to_string()),
        }?;
        Ok(())
    }

    pub fn is_plugin_installed(&self, id: &str) -> Result<bool, String> {
        self.ensure_initialized()?;
        match self.plugin_repository.as_ref() {
            Some(repo) => repo.is_plugin_exists(id),
            None => Err("PluginRepository is not initialized".to_string()),
        }
    }

    pub fn get_installed_plugin(&self, id: &str) -> Result<Option<Plugin>, String> {
        self.ensure_initialized()?;
        match self.plugin_repository.as_ref() {
            Some(repo) => repo.get_plugin(id),
            None => Err("PluginRepository is not initialized".to_string()),
        }
    }

    pub fn get_installed_plugins(&self) -> Result<Vec<Plugin>, String> {
        self.ensure_initialized()?;
        match self.plugin_repository.as_ref() {
            Some(repo) => repo.get_plugins(),
            None => Err("PluginRepository is not initialized".to_string()),
        }
    }

    /// Uninstall a plugin by id
    pub fn uninstall_plugin(&mut self, id: &str) -> Result<(), String> {
        self.ensure_initialized()?;
        match self.plugin_repository.as_ref() {
            Some(repo) => repo.delete_plugin(id),
            None => Err("PluginRepository is not initialized".to_string()),
        }?;
        match self.dataset_repository.as_ref() {
            Some(repo) => repo.delete_datasets(id),
            None => Err("DatasetRepository is not initialized".to_string()),
        }?;
        Ok(())
    }

    pub fn request_dataset(&self, plugin_id: &str, dataset_id: &str) -> Result<String, String> {
        self.ensure_initialized()?;
        let result = match self.dataset_repository.as_ref() {
            Some(repo) => repo.request_dataset(plugin_id, dataset_id)?,
            None => return Err("DatasetRepository is not initialized".to_string()),
        };
        Ok(result)
    }

    fn ensure_initialized(&self) -> Result<(), String> {
        if self.config.is_none() {
            return Err("The plugin manager is not initialized".to_string());
        }
        Ok(())
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
