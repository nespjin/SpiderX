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

use crate::{
    database::{database, dataset_dao, plugin_dao},
    repository::model::plugin,
};

pub(crate) struct PluginRepository {
    database_path: String,
}

impl PluginRepository {
    pub fn new(database_path: String) -> PluginRepository {
        PluginRepository { database_path }
    }

    pub fn save_plugin(&self, plugin: Plugin) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entities = plugin::plugins_to_entities(vec![plugin])?;
        let result =
            plugin_dao::upsert_all(&mut sqlite_connection, &entities).map_err(|e| e.to_string())?;
        if result == 0 {
            return Err("PluginRepository save_plugin failed".to_string());
        }
        Ok(())
    }

    pub fn save_plugins(&self, plugins: Vec<Plugin>) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entities = plugin::plugins_to_entities(plugins)?;
        let result =
            plugin_dao::upsert_all(&mut sqlite_connection, &entities).map_err(|e| e.to_string())?;
        if result == 0 {
            return Err("PluginRepository save_plugins failed".to_string());
        }
        Ok(())
    }

    pub fn get_plugins(&self) -> Result<Vec<Plugin>, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        
        // Fetch all plugins
        let entities = plugin_dao::find_all(&mut sqlite_connection).map_err(|e| e.to_string())?;
        
        if entities.is_empty() {
            return Ok(Vec::new());
        }
        
        // Collect all plugin IDs
        let plugin_ids: Vec<String> = entities.iter().map(|e| e.id.clone()).collect();
        
        // Single query to fetch all datasets for all plugins (fixes N+1 problem)
        let all_datasets = dataset_dao::find_by_plugin_ids(&mut sqlite_connection, &plugin_ids)
            .map_err(|e| e.to_string())?;
        
        // Group datasets by plugin_id
        let mut datasets_by_plugin: std::collections::HashMap<String, Vec<_>> = 
            std::collections::HashMap::new();
        for dataset in all_datasets {
            datasets_by_plugin
                .entry(dataset.plugin_id.clone())
                .or_insert_with(Vec::new)
                .push(dataset);
        }
        
        // Build dataset_entities in the same order as entities
        let dataset_entities: Vec<Vec<_>> = entities
            .iter()
            .map(|entity| {
                datasets_by_plugin
                    .get(&entity.id)
                    .cloned()
                    .unwrap_or_default()
            })
            .collect();
        
        let plugins = plugin::entities_to_external_models(entities, dataset_entities)?;
        Ok(plugins)
    }

    pub fn get_plugin(&self, id: &str) -> Result<Option<Plugin>, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        
        let entity =
            plugin_dao::find_by_id(&mut sqlite_connection, id).map_err(|e| e.to_string())?;
        
        match entity {
            Some(entity) => {
                // Fetch datasets for this specific plugin
                let dataset_entities = dataset_dao::find_by_plugin_id(&mut sqlite_connection, id)
                    .map_err(|e| e.to_string())?;
                let plugin = plugin::entity_to_external_model(entity, dataset_entities)?;
                Ok(Some(plugin))
            }
            None => Ok(None),
        }
    }

    pub fn is_plugin_exists(&self, id: &str) -> Result<bool, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        plugin_dao::is_plugin_exists(&mut sqlite_connection, id).map_err(|e| e.to_string())
    }

    pub fn delete_plugin(&self, id: &str) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let _ = plugin_dao::delete_by_id(&mut sqlite_connection, id).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub fn delete_plugins(&self) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let _ = plugin_dao::delete_all(&mut sqlite_connection).map_err(|e| e.to_string())?;
        Ok(())
    }
}
