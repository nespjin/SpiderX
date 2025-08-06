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
use std::sync::{Arc, Mutex, OnceLock};

use compiler::{json_plugin_compiler::JsonPluginCompiler, plugin_compiler::PluginCompiler};

use crate::repository::{
    dataset_repository::DatasetRepository,
    plugin_repository::{self, PluginRepository},
};

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
    fn get_instance() -> Arc<Mutex<PluginManager>> {
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

    pub fn init(&mut self, config: PluginManagerConfig) {
        self.plugin_repository = Some(PluginRepository::new(config.database_path.clone()));
        self.dataset_repository = Some(DatasetRepository::new(config.database_path.clone()));
        self.config = Some(config);
    }

    pub fn database_path(&self) -> Option<String> {
        self.config
            .as_ref()
            .map(|config| config.database_path.clone())
    }

    pub fn install_plugin(&self, source: &PluginSource) -> Result<(), String> {
        match source {
            PluginSource::ManifestJson(json) => self.install_plugin_from_manifest_json(json),
            PluginSource::ManifestJsonFile(path) => todo!(),
        }
    }

    fn install_plugin_from_manifest_json(&self, json: &String) -> Result<(), String> {
        let compiler_result = JsonPluginCompiler::parse(json);
        if let Err(error) = compiler_result {
            return Err(error.to_string());
        }
        let compiler = compiler_result.unwrap();
        self.do_install_plugin(&compiler.compile()?);
        Ok(())
    }

    fn do_install_plugin(&self, plugin: &Plugin) -> Result<(), String> {
        todo!()
    }

    /// Uninstall a plugin by id
    pub fn uninstall_plugin(&mut self, id: &str) -> Result<(), String> {
        self.ensure_initialized()?;
        self.plugin_repository.as_ref().unwrap().delete_plugin(id)?;
        self.dataset_repository
            .as_ref()
            .unwrap()
            .delete_datasets(id)?;
        Ok(())
    }

    fn ensure_initialized(&self) -> Result<(), String> {
        if self.config.is_none() {
            return Err("Not initialized".to_string());
        }
        Ok(())
    }
}
