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
    sync::{OnceLock, RwLock},
};

use compiler::{json_plugin_compiler::JsonPluginCompiler, plugin_compiler::PluginCompiler};

use crate::{
    cache::CacheManager,
    database::{connection_pool::DatabasePool, entities::plugin::PluginEntity},
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

#[derive(Debug, Clone)]
pub enum PluginSource {
    ManifestJson(String),
    ManifestJsonFile(String),
}

/// Custom error type for PluginManager operations
#[derive(Debug)]
pub enum PluginManagerError {
    NotInitialized,
    InitializationFailed(String),
    RepositoryAccessFailed(String),
    DatabaseError(String),
    IoError(String),
    CompilationError(String),
    OperationFailed(String),
}

impl std::fmt::Display for PluginManagerError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            PluginManagerError::NotInitialized => {
                write!(f, "PluginManager is not initialized")
            }
            PluginManagerError::InitializationFailed(msg) => {
                write!(f, "Initialization failed: {}", msg)
            }
            PluginManagerError::RepositoryAccessFailed(msg) => {
                write!(f, "Repository access failed: {}", msg)
            }
            PluginManagerError::DatabaseError(msg) => write!(f, "Database error: {}", msg),
            PluginManagerError::IoError(msg) => write!(f, "IO error: {}", msg),
            PluginManagerError::CompilationError(msg) => {
                write!(f, "Compilation error: {}", msg)
            },
            PluginManagerError::OperationFailed(msg) => write!(f, "Operation error: {}", msg),
        }
    }
}

impl From<String> for PluginManagerError {
    fn from(error: String) -> Self {
        PluginManagerError::InitializationFailed(error)
    }
}

impl From<std::io::Error> for PluginManagerError {
    fn from(error: std::io::Error) -> Self {
        PluginManagerError::IoError(error.to_string())
    }
}

pub struct PluginManager {
    config: OnceLock<PluginManagerConfig>,
    dataset_repository: RwLock<Option<DatasetRepository>>,
    plugin_repository: RwLock<Option<PluginRepository>>,
    request_javascript_dataset_config: RwLock<Option<RequestJavaScriptDatasetConfigArc>>,
}

impl PluginManager {
    pub fn get_instance() -> &'static PluginManager {
        static INSTANCE: OnceLock<PluginManager> = OnceLock::new();
        INSTANCE.get_or_init(|| PluginManager {
            config: OnceLock::new(),
            dataset_repository: RwLock::new(None),
            plugin_repository: RwLock::new(None),
            request_javascript_dataset_config: RwLock::new(None),
        })
    }

    pub fn init(&self, config: PluginManagerConfig) -> Result<(), PluginManagerError> {
        // Check if already initialized
        if self.config.get().is_some() {
            return Err(PluginManagerError::InitializationFailed(
                "PluginManager has been initialized.".to_string(),
            ));
        }

        #[cfg(target_os = "android")]
        {
            use android_logger::Config;
            android_logger::init_once(
                Config::default()
                    .with_tag("SpiderX")
                    .with_max_level(log::LevelFilter::Trace),
            );
        }

        let database_path_str = &config.database_path;
        let database_path = Path::new(database_path_str);

        // Create parent directory if it doesn't exist
        if let Some(parent) = database_path.parent() {
            if !parent.exists() {
                fs::create_dir_all(parent).map_err(PluginManagerError::from)?;
            }
        }

        // Initialize connection pool
        DatabasePool::init(database_path_str)?;
        log::info!("Connection pool initialized for: {}", database_path_str);

        // Initialize cache manager
        CacheManager::init();
        log::info!("Cache manager initialized");

        // Initialize repositories
        {
            let mut plugin_repo = self
                .plugin_repository
                .write()
                .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
            *plugin_repo = Some(PluginRepository::new());
        }

        {
            let mut dataset_repo = self
                .dataset_repository
                .write()
                .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
            *dataset_repo = Some(DatasetRepository::new());
        }

        // Run migrations using pooled connection
        let pool = DatabasePool::get_instance()
            .get()
            .ok_or_else(|| PluginManagerError::DatabaseError("Failed to get database pool instance".to_string()))?;
        pool.run_migrations()?;

        // Store config last (signals successful initialization)
        self.config
            .set(config)
            .map_err(|_| PluginManagerError::InitializationFailed("Failed to set config".to_string()))?;

        log::info!("PluginManager initialized successfully with connection pooling and caching");
        Ok(())
    }

    pub fn set_request_javascript_dataset_config(
        &self,
        request_javascript_dataset_config: RequestJavaScriptDatasetConfigArc,
    ) {
        let mut cfg = self
            .request_javascript_dataset_config
            .write()
            .expect("Failed to acquire write lock");
        *cfg = Some(request_javascript_dataset_config);
    }

    pub fn get_request_javascript_dataset_config(
        &self,
    ) -> Option<RequestJavaScriptDatasetConfigArc> {
        self.request_javascript_dataset_config
            .read()
            .expect("Failed to acquire read lock")
            .clone()
    }

    pub fn install_plugin(&self, source: &PluginSource) -> Result<(), PluginManagerError> {
        match source {
            PluginSource::ManifestJson(json) => self.install_plugin_from_manifest_json(json),
            PluginSource::ManifestJsonFile(path) => {
                let json = fs::read_to_string(path)
                    .map_err(|err| PluginManagerError::IoError(format!("Failed to read plugin manifest file: {}", err)))?;
                self.install_plugin_from_manifest_json(&json)
            }
        }
    }

    fn install_plugin_from_manifest_json(&self, json: &str) -> Result<(), PluginManagerError> {
        let compiler = JsonPluginCompiler::parse(json)
            .map_err(|e| PluginManagerError::CompilationError(e.to_string()))?;
        self.do_install_plugin(compiler.compile()?)
    }

    fn do_install_plugin(&self, plugin: Plugin) -> Result<(), PluginManagerError> {
        self.ensure_initialized()?;
        let plugin_id = plugin.id.to_string();
        let datasets = plugin.datasets.clone();

        {
            let repo = self
                .plugin_repository
                .read()
                .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
            let repo = repo
                .as_ref()
                .ok_or(PluginManagerError::NotInitialized)?;
            repo.save_plugin(plugin)?;
        }

        {
            let repo = self
                .dataset_repository
                .read()
                .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
            let repo = repo
                .as_ref()
                .ok_or(PluginManagerError::NotInitialized)?;
            repo.save_datasets(&plugin_id, datasets)?;
        }

        Ok(())
    }

    pub fn is_plugin_installed(&self, id: &str) -> Result<bool, PluginManagerError> {
        self.ensure_initialized()?;
        let repo = self
            .plugin_repository
            .read()
            .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
        let repo = repo.as_ref().ok_or(PluginManagerError::NotInitialized)?;
        repo.is_plugin_exists(id).map_err(PluginManagerError::OperationFailed)
    }

    pub fn get_installed_plugin(&self, id: &str) -> Result<Option<Plugin>, PluginManagerError> {
        self.ensure_initialized()?;
        let repo = self
            .plugin_repository
            .read()
            .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
        let repo = repo.as_ref().ok_or(PluginManagerError::NotInitialized)?;
        repo.get_plugin(id).map_err(PluginManagerError::OperationFailed)
    }

    pub fn get_installed_plugins(&self) -> Result<Vec<Plugin>, PluginManagerError> {
        self.ensure_initialized()?;
        let repo = self
            .plugin_repository
            .read()
            .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
        let repo = repo.as_ref().ok_or(PluginManagerError::NotInitialized)?;
        repo.get_plugins().map_err(PluginManagerError::OperationFailed)
    }

    /// Uninstall a plugin by id
    pub fn uninstall_plugin(&self, id: &str) -> Result<(), PluginManagerError> {
        self.ensure_initialized()?;

        {
            let repo = self
                .plugin_repository
                .read()
                .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
            let repo = repo.as_ref().ok_or(PluginManagerError::NotInitialized)?;
            repo.delete_plugin(id)?;
        }

        {
            let repo = self
                .dataset_repository
                .read()
                .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
            let repo = repo.as_ref().ok_or(PluginManagerError::NotInitialized)?;
            repo.delete_datasets(id)?;
        }

        Ok(())
    }

    pub fn request_dataset(
        &self,
        plugin_id: &str,
        dataset_id: &str,
        r#type: RequestType,
        listener: Option<RequestDatasetListenerWrpper>,
    ) -> Result<String, PluginManagerError> {
        self.ensure_initialized()?;
        
        let config = self
            .request_javascript_dataset_config
            .read()
            .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?
            .clone();

        let repo = self
            .dataset_repository
            .read()
            .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
        let repo = repo.as_ref().ok_or(PluginManagerError::NotInitialized)?;
        repo.request_dataset(plugin_id, dataset_id, r#type, listener, config).map_err(PluginManagerError::OperationFailed)
    }

    pub fn auto_request_type(
        &self,
        plugin_id: &str,
        dataset_id: &str,
    ) -> Result<RequestType, PluginManagerError> {
        self.ensure_initialized()?;
        let repo = self
            .dataset_repository
            .read()
            .map_err(|e| PluginManagerError::RepositoryAccessFailed(e.to_string()))?;
        let repo = repo.as_ref().ok_or(PluginManagerError::NotInitialized)?;
        repo.auto_request_type(plugin_id, dataset_id).map_err(PluginManagerError::OperationFailed)
    }

    fn ensure_initialized(&self) -> Result<(), PluginManagerError> {
        if self.config.get().is_none() {
            return Err(PluginManagerError::NotInitialized);
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
#[repr(i32)]
pub enum RequestType {
    Auto = 0,
    JavaScript = 1,
    Dsl = 2,
}

impl TryFrom<i32> for RequestType {
    type Error = ();

    fn try_from(value: i32) -> Result<Self, Self::Error> {
        match value {
            0 => Ok(RequestType::Auto),
            1 => Ok(RequestType::JavaScript),
            2 => Ok(RequestType::Dsl),
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
        let config = PluginManagerConfig {
            database_path: "target/runtime.db".to_string(),
        };
        let result = plugin_manager.init(config.clone());
        assert!(result.is_ok());
        
        let result = plugin_manager.init(config.clone());
        assert!(result.is_err());
        assert!(matches!(
            result.unwrap_err(),
            PluginManagerError::InitializationFailed(_)
        ));
    }
}
