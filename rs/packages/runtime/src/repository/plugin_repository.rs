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
    cache::CacheManager,
    database::{connection_pool::get_pooled_connection, dataset_dao, plugin_dao},
    repository::model::{dataset::datasets_entities_to_external_models, plugin},
};

pub(crate) struct PluginRepository {}

impl PluginRepository {
    pub fn new() -> PluginRepository {
        PluginRepository {}
    }

    pub fn save_plugin(&self, plugin: Plugin) -> Result<(), String> {
        let cache = CacheManager::init();

        // Invalidate old cache entry if this is an update
        cache.invalidate_plugin_update(&plugin.id);

        let mut pooled_conn = get_pooled_connection()?;
        let entities = plugin::plugins_to_entities(vec![plugin.clone()])?;
        let result =
            plugin_dao::upsert_all(&mut *pooled_conn, &entities).map_err(|e| e.to_string())?;
        if result == 0 {
            return Err("PluginRepository save_plugin failed".to_string());
        }

        // Update cache with new data
        cache.insert_plugin(plugin.id.clone(), plugin.clone());
        log::debug!("Updated cache for plugin: {}", plugin.id);

        Ok(())
    }

    pub fn save_plugins(&self, plugins: Vec<Plugin>) -> Result<(), String> {
        let mut pooled_conn = get_pooled_connection()?;
        let entities = plugin::plugins_to_entities(plugins.clone())?;
        let result =
            plugin_dao::upsert_all(&mut *pooled_conn, &entities).map_err(|e| e.to_string())?;
        if result == 0 {
            return Err("PluginRepository save_plugins failed".to_string());
        }

        // Update cache for all plugins
        let cache = CacheManager::init();
        for plugin in &plugins {
            cache.insert_plugin(plugin.id.clone(), plugin.clone());
        }

        Ok(())
    }

    pub fn get_plugins(&self) -> Result<Vec<Plugin>, String> {
        let cache = CacheManager::init();

        // Try to get all plugins from individual cache entries
        // For bulk operations, we still need to query DB but can cache results
        let mut pooled_conn = get_pooled_connection()?;

        // Fetch all plugins
        let entities = plugin_dao::find_all(&mut *pooled_conn).map_err(|e| e.to_string())?;

        if entities.is_empty() {
            return Ok(Vec::new());
        }

        // Collect all plugin IDs
        let plugin_ids: Vec<String> = entities.iter().map(|e| e.id.clone()).collect();

        // Single query to fetch all datasets for all plugins (fixes N+1 problem)
        let all_datasets = dataset_dao::find_by_plugin_ids(&mut *pooled_conn, &plugin_ids)
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

        // Cache all plugins and their datasets
        for plugin in &plugins {
            cache.insert_plugin(plugin.id.clone(), plugin.clone());
            if let Some(datasets) = datasets_by_plugin
                .get(&plugin.id)
                .map(|e| e.clone())
                .map(datasets_entities_to_external_models)
                .map(|e| e.ok())
                .flatten()
            {
                // Convert entities to external models for caching
                // Note: This is simplified - in production you'd want to cache the converted datasets
                cache.insert_plugin_datasets(plugin.id.clone(), datasets);
            }
        }

        log::debug!("Loaded {} plugins, cache updated", plugins.len());
        Ok(plugins)
    }

    pub fn get_plugin(&self, id: &str) -> Result<Option<Plugin>, String> {
        let cache = CacheManager::init();

        // Check cache first
        if let Some(cached) = cache.get_plugin(id) {
            log::debug!("Cache hit for plugin: {}", id);
            return Ok(Some(cached));
        }

        log::debug!("Cache miss for plugin: {}, querying database", id);

        // Get from database using pooled connection
        let mut pooled_conn = get_pooled_connection()?;

        let entity = plugin_dao::find_by_id(&mut *pooled_conn, id).map_err(|e| e.to_string())?;

        match entity {
            Some(entity) => {
                // Fetch datasets for this specific plugin
                let dataset_entities = dataset_dao::find_by_plugin_id(&mut *pooled_conn, id)
                    .map_err(|e| e.to_string())?;
                let plugin = plugin::entity_to_external_model(entity, dataset_entities)?;

                // Cache the result
                cache.insert_plugin(id.to_string(), plugin.clone());
                log::debug!("Cached plugin: {}", id);

                Ok(Some(plugin))
            }
            None => Ok(None),
        }
    }

    pub fn is_plugin_exists(&self, id: &str) -> Result<bool, String> {
        // Check cache first
        let cache = CacheManager::init();
        if cache.get_plugin(id).is_some() {
            return Ok(true);
        }

        let mut pooled_conn = get_pooled_connection()?;
        plugin_dao::is_plugin_exists(&mut *pooled_conn, id).map_err(|e| e.to_string())
    }

    pub fn delete_plugin(&self, id: &str) -> Result<(), String> {
        let mut pooled_conn = get_pooled_connection()?;
        let _ = plugin_dao::delete_by_id(&mut *pooled_conn, id).map_err(|e| e.to_string())?;

        // Invalidate cache
        let cache = CacheManager::init();
        cache.remove_plugin(id);
        log::debug!("Invalidated cache for deleted plugin: {}", id);

        Ok(())
    }

    pub fn delete_plugins(&self) -> Result<(), String> {
        let mut pooled_conn = get_pooled_connection()?;
        let _ = plugin_dao::delete_all(&mut *pooled_conn).map_err(|e| e.to_string())?;

        // Clear all caches (both plugins and datasets)
        let cache = CacheManager::init();
        cache.clear_all();
        log::debug!("Cleared all caches after deleting all plugins");

        Ok(())
    }
}
