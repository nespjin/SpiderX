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

use spiderx_core::data::plugin::Plugin;
use std::{
    fs,
    path::Path,
    sync::{OnceLock, RwLock},
};

use compiler::{json_plugin_compiler::JsonPluginCompiler, plugin_compiler::PluginCompiler};

use crate::{
    cache::CacheManager,
    database::connection_pool::DatabasePool,
    executor::request_javascript_dataset_config::RequestJavaScriptDatasetConfigArc,
    repository::{
        dataset_repository::DatasetRepository, plugin_repository::PluginRepository,
        request_dataset_options::RequestDatasetOptions,
    },
};

/// Configuration for PluginManager
#[derive(Debug, Clone)]
pub struct PluginManagerConfig {
    /// Path to the SQLite database file
    pub database_path: String,
}

/// Source type for plugin installation
#[derive(Debug, Clone)]
pub enum PluginSource {
    /// Plugin manifest as a JSON string
    ManifestJson(String),
    /// Path to a plugin manifest JSON file
    ManifestJsonFile(String),
}

/// Custom error type for PluginManager operations
#[derive(Debug)]
pub enum PluginManagerError {
    /// PluginManager has not been initialized
    NotInitialized,
    /// Initialization failed with a specific error message
    InitializationFailed(String),
    /// Failed to access repository (lock errors, etc.)
    RepositoryAccessFailed(String),
    /// Database operation failed
    DatabaseError(String),
    /// I/O operation failed
    IoError(String),
    /// Plugin compilation failed
    CompilationError(String),
    /// General operation failed
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
            }
            PluginManagerError::OperationFailed(msg) => write!(f, "Operation error: {}", msg),
        }
    }
}

impl std::error::Error for PluginManagerError {}

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

/// Manages plugin lifecycle including installation, uninstallation, and dataset requests
///
/// The PluginManager is a singleton that handles:
/// - Plugin installation from JSON manifests
/// - Plugin uninstallation with cleanup
/// - Dataset requests to installed plugins
/// - Repository and cache management
pub struct PluginManager {
    /// Configuration (set once during initialization)
    config: OnceLock<PluginManagerConfig>,
    /// Repository for dataset operations (protected by RwLock for concurrent reads)
    dataset_repository: RwLock<Option<DatasetRepository>>,
    /// Repository for plugin operations (protected by RwLock for concurrent reads)
    plugin_repository: RwLock<Option<PluginRepository>>,
    /// Configuration for JavaScript dataset requests
    request_javascript_dataset_config: RwLock<Option<RequestJavaScriptDatasetConfigArc>>,
}

impl PluginManager {
    /// Get the singleton instance of PluginManager
    pub fn get_instance() -> &'static PluginManager {
        static INSTANCE: OnceLock<PluginManager> = OnceLock::new();
        INSTANCE.get_or_init(|| PluginManager {
            config: OnceLock::new(),
            dataset_repository: RwLock::new(None),
            plugin_repository: RwLock::new(None),
            request_javascript_dataset_config: RwLock::new(None),
        })
    }

    /// Initialize the PluginManager with the given configuration
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
            use android_logger::FilterBuilder;
            use log::LevelFilter;
            android_logger::init_once(
                Config::default()
                    .with_tag("SpiderX")
                    .with_max_level(log::LevelFilter::Trace), // .with_filter(
                                                              //     FilterBuilder::new()
                                                              //         .parse("runtime=debug,jni::wrapper=off")
                                                              //         .build(),
                                                              // ),
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
        self.init_plugin_repository()?;
        self.init_dataset_repository()?;

        // Run migrations using pooled connection
        let pool = DatabasePool::get_instance().get().ok_or_else(|| {
            PluginManagerError::DatabaseError("Failed to get database pool instance".to_string())
        })?;
        pool.run_migrations()?;

        // Store config last (signals successful initialization)
        self.config.set(config).map_err(|_| {
            PluginManagerError::InitializationFailed("Failed to set config".to_string())
        })?;

        log::info!("PluginManager initialized successfully with connection pooling and caching");
        Ok(())
    }

    /// Initialize the plugin repository
    fn init_plugin_repository(&self) -> Result<(), PluginManagerError> {
        let mut plugin_repo = self.plugin_repository.write().map_err(|e| {
            PluginManagerError::RepositoryAccessFailed(format!(
                "Failed to acquire write lock for plugin repository: {}",
                e
            ))
        })?;
        *plugin_repo = Some(PluginRepository::new());
        log::debug!("Plugin repository initialized");
        Ok(())
    }

    /// Initialize the dataset repository
    fn init_dataset_repository(&self) -> Result<(), PluginManagerError> {
        let mut dataset_repo = self.dataset_repository.write().map_err(|e| {
            PluginManagerError::RepositoryAccessFailed(format!(
                "Failed to acquire write lock for dataset repository: {}",
                e
            ))
        })?;
        *dataset_repo = Some(DatasetRepository::new());
        log::debug!("Dataset repository initialized");
        Ok(())
    }

    /// Helper method to safely access the plugin repository
    fn with_plugin_repository<F, R>(&self, f: F) -> Result<R, PluginManagerError>
    where
        F: FnOnce(&PluginRepository) -> Result<R, PluginManagerError>,
    {
        let repo_guard = self.plugin_repository.read().map_err(|e| {
            PluginManagerError::RepositoryAccessFailed(format!(
                "Failed to acquire read lock for plugin repository: {}",
                e
            ))
        })?;

        let repo = repo_guard
            .as_ref()
            .ok_or(PluginManagerError::NotInitialized)?;

        f(repo)
    }

    /// Helper method to safely access the dataset repository
    fn with_dataset_repository<F, R>(&self, f: F) -> Result<R, PluginManagerError>
    where
        F: FnOnce(&DatasetRepository) -> Result<R, PluginManagerError>,
    {
        let repo_guard = self.dataset_repository.read().map_err(|e| {
            PluginManagerError::RepositoryAccessFailed(format!(
                "Failed to acquire read lock for dataset repository: {}",
                e
            ))
        })?;

        let repo = repo_guard
            .as_ref()
            .ok_or(PluginManagerError::NotInitialized)?;

        f(repo)
    }

    /// Set the JavaScript dataset request configuration
    pub fn set_request_javascript_dataset_config(
        &self,
        request_javascript_dataset_config: RequestJavaScriptDatasetConfigArc,
    ) {
        let mut cfg = self
            .request_javascript_dataset_config
            .write()
            .expect("Failed to acquire write lock for request_javascript_dataset_config");
        *cfg = Some(request_javascript_dataset_config);
    }

    /// Get the JavaScript dataset request configuration
    pub fn get_request_javascript_dataset_config(
        &self,
    ) -> Option<RequestJavaScriptDatasetConfigArc> {
        self.request_javascript_dataset_config
            .read()
            .expect("Failed to acquire read lock for request_javascript_dataset_config")
            .clone()
    }

    /// Install a plugin from a manifest JSON string or file path
    pub fn install_plugin(&self, source: &PluginSource) -> Result<(), PluginManagerError> {
        match source {
            PluginSource::ManifestJson(json) => self.install_plugin_from_manifest_json(json),
            PluginSource::ManifestJsonFile(path) => {
                let json = fs::read_to_string(path).map_err(|err| {
                    PluginManagerError::IoError(format!(
                        "Failed to read plugin manifest file: {}",
                        err
                    ))
                })?;
                self.install_plugin_from_manifest_json(&json)
            }
        }
    }

    /// Install a plugin from a JSON manifest string
    fn install_plugin_from_manifest_json(&self, json: &str) -> Result<(), PluginManagerError> {
        let compiler = JsonPluginCompiler::parse(json).map_err(|e| {
            PluginManagerError::CompilationError(format!("Failed to parse plugin manifest: {}", e))
        })?;
        self.do_install_plugin(compiler.compile()?)
    }

    /// Internal method to perform the actual plugin installation
    fn do_install_plugin(&self, plugin: Plugin) -> Result<(), PluginManagerError> {
        self.ensure_initialized()?;
        let plugin_id = plugin.id.to_string();
        let datasets = plugin.datasets.clone();

        // Save plugin to repository
        self.with_plugin_repository(|repo| {
            repo.save_plugin(plugin)
                .map_err(PluginManagerError::OperationFailed)
        })?;

        // Save datasets to repository
        self.with_dataset_repository(|repo| {
            repo.save_datasets(&plugin_id, datasets)
                .map_err(PluginManagerError::OperationFailed)
        })?;

        log::info!("Plugin installed successfully: {}", plugin_id);
        Ok(())
    }

    /// Check if a plugin is installed by its ID
    pub fn is_plugin_installed(&self, id: &str) -> Result<bool, PluginManagerError> {
        self.ensure_initialized()?;
        self.with_plugin_repository(|repo| {
            repo.is_plugin_exists(id)
                .map_err(PluginManagerError::OperationFailed)
        })
    }

    /// Get an installed plugin by its ID
    pub fn get_installed_plugin(&self, id: &str) -> Result<Option<Plugin>, PluginManagerError> {
        self.ensure_initialized()?;
        self.with_plugin_repository(|repo| {
            repo.get_plugin(id)
                .map_err(PluginManagerError::OperationFailed)
        })
    }

    /// Get all installed plugins
    pub fn get_installed_plugins(&self) -> Result<Vec<Plugin>, PluginManagerError> {
        self.ensure_initialized()?;
        self.with_plugin_repository(|repo| {
            repo.get_plugins()
                .map_err(PluginManagerError::OperationFailed)
        })
    }

    /// Uninstall a plugin by its ID
    pub fn uninstall_plugin(&self, id: &str) -> Result<(), PluginManagerError> {
        self.ensure_initialized()?;

        // Delete plugin from repository
        self.with_plugin_repository(|repo| {
            repo.delete_plugin(id)
                .map_err(PluginManagerError::OperationFailed)
        })?;

        // Delete associated datasets
        self.with_dataset_repository(|repo| {
            repo.delete_datasets(id)
                .map_err(PluginManagerError::OperationFailed)
        })?;

        log::info!("Plugin uninstalled successfully: {}", id);
        Ok(())
    }

    /// Request a dataset from a plugin
    pub fn request_dataset(
        &self,
        plugin_id: &str,
        dataset_id: &str,
        mut options: RequestDatasetOptions,
    ) -> Result<String, PluginManagerError> {
        self.ensure_initialized()?;

        // Get JavaScript config
        let config = self.get_request_javascript_dataset_config();
        options.with_opt_config(config);

        // Request dataset from repository
        self.with_dataset_repository(|repo| {
            repo.request_dataset(plugin_id, dataset_id, options)
                .map_err(PluginManagerError::OperationFailed)
        })
    }

    /// Determine the automatic request type for a dataset
    pub fn auto_request_type(
        &self,
        plugin_id: &str,
        dataset_id: &str,
    ) -> Result<RequestType, PluginManagerError> {
        self.ensure_initialized()?;
        self.with_dataset_repository(|repo| {
            repo.auto_request_type(plugin_id, dataset_id)
                .map_err(PluginManagerError::OperationFailed)
        })
    }

    /// Check if the PluginManager has been initialized
    fn ensure_initialized(&self) -> Result<(), PluginManagerError> {
        if self.config.get().is_none() {
            return Err(PluginManagerError::NotInitialized);
        }
        Ok(())
    }
}

/// Request type for dataset execution
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
#[repr(i32)]
pub enum RequestType {
    /// Automatically determine the request type
    Auto = 0,
    /// Use JavaScript executor
    JavaScript = 1,
    /// Use DSL executor
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
