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

use spiderx_core::data::plugin::{Dataset, Plugin};
use lru::LruCache;
use std::num::NonZeroUsize;
use std::sync::{Mutex, OnceLock};

/// Cache manager using LRU (Least Recently Used) strategy
pub struct CacheManager {
    /// Cache for plugins: plugin_id -> Plugin
    plugin_cache: Mutex<LruCache<String, Plugin>>,
    /// Cache for datasets: (plugin_id, dataset_id) -> Dataset
    dataset_cache: Mutex<LruCache<(String, String), Dataset>>,
    /// Cache for all datasets by plugin: plugin_id -> Vec<Dataset>
    plugin_datasets_cache: Mutex<LruCache<String, Vec<Dataset>>>,
}

impl CacheManager {
    /// Get the singleton instance
    pub fn get_instance() -> &'static OnceLock<CacheManager> {
        static INSTANCE: OnceLock<CacheManager> = OnceLock::new();
        &INSTANCE
    }

    /// Initialize the cache manager
    pub fn init() -> &'static CacheManager {
        let instance = Self::get_instance();
        instance.get_or_init(|| {
            // Create caches with reasonable sizes
            let plugin_cache_size = NonZeroUsize::new(100).unwrap(); // Cache up to 100 plugins
            let dataset_cache_size = NonZeroUsize::new(500).unwrap(); // Cache up to 500 datasets
            let plugin_datasets_size = NonZeroUsize::new(100).unwrap(); // Cache datasets for 100 plugins

            CacheManager {
                plugin_cache: Mutex::new(LruCache::new(plugin_cache_size)),
                dataset_cache: Mutex::new(LruCache::new(dataset_cache_size)),
                plugin_datasets_cache: Mutex::new(LruCache::new(plugin_datasets_size)),
            }
        })
    }

    // ========== Plugin Cache Operations ==========

    /// Get a plugin from cache
    pub fn get_plugin(&self, plugin_id: &str) -> Option<Plugin> {
        let mut cache = self.plugin_cache.lock().unwrap();
        cache.get(plugin_id).cloned()
    }

    /// Insert a plugin into cache
    pub fn insert_plugin(&self, plugin_id: String, plugin: Plugin) {
        let mut cache = self.plugin_cache.lock().unwrap();
        cache.put(plugin_id, plugin);
    }

    /// Remove a plugin from cache
    pub fn remove_plugin(&self, plugin_id: &str) {
        let mut cache = self.plugin_cache.lock().unwrap();
        cache.pop(plugin_id);

        // Also remove associated datasets
        self.remove_plugin_datasets(plugin_id);
    }

    /// Clear all plugin caches
    pub fn clear_plugin_cache(&self) {
        let mut cache = self.plugin_cache.lock().unwrap();
        cache.clear();
    }

    // ========== Dataset Cache Operations ==========

    /// Get a dataset from cache
    pub fn get_dataset(&self, plugin_id: &str, dataset_id: &str) -> Option<Dataset> {
        let mut cache = self.dataset_cache.lock().unwrap();
        cache
            .get(&(plugin_id.to_string(), dataset_id.to_string()))
            .cloned()
    }

    /// Insert a dataset into cache
    pub fn insert_dataset(&self, plugin_id: String, dataset_id: String, dataset: Dataset) {
        let mut cache = self.dataset_cache.lock().unwrap();
        cache.put((plugin_id, dataset_id), dataset);
    }

    /// Remove a dataset from cache
    pub fn remove_dataset(&self, plugin_id: &str, dataset_id: &str) {
        let mut cache = self.dataset_cache.lock().unwrap();
        cache.pop(&(plugin_id.to_string(), dataset_id.to_string()));
    }

    // ========== Plugin Datasets Cache Operations ==========

    /// Get all datasets for a plugin from cache
    pub fn get_plugin_datasets(&self, plugin_id: &str) -> Option<Vec<Dataset>> {
        let mut cache = self.plugin_datasets_cache.lock().unwrap();
        cache.get(plugin_id).cloned()
    }

    /// Insert all datasets for a plugin into cache
    pub fn insert_plugin_datasets(&self, plugin_id: String, datasets: Vec<Dataset>) {
        // Also cache individual datasets
        for dataset in &datasets {
            self.insert_dataset(plugin_id.clone(), dataset.id.clone(), dataset.clone());
        }

        let mut cache = self.plugin_datasets_cache.lock().unwrap();
        cache.put(plugin_id, datasets);
    }

    /// Remove all datasets for a plugin from cache
    pub fn remove_plugin_datasets(&self, plugin_id: &str) {
        // Remove individual datasets
        if let Some(datasets) = self.get_plugin_datasets(plugin_id) {
            let mut cache = self.dataset_cache.lock().unwrap();
            for dataset in &datasets {
                cache.pop(&(plugin_id.to_string(), dataset.id.clone()));
            }
        }

        // Remove the list
        let mut cache = self.plugin_datasets_cache.lock().unwrap();
        cache.pop(plugin_id);
    }

    /// Clear all dataset caches
    pub fn clear_dataset_cache(&self) {
        let mut cache = self.dataset_cache.lock().unwrap();
        cache.clear();

        let mut cache = self.plugin_datasets_cache.lock().unwrap();
        cache.clear();
    }

    // ========== Utility Operations ==========

    /// Clear all caches
    pub fn clear_all(&self) {
        self.clear_plugin_cache();
        self.clear_dataset_cache();
        log::info!("All caches cleared");
    }

    /// Invalidate cache for a plugin update (removes old data before inserting new)
    pub fn invalidate_plugin_update(&self, plugin_id: &str) {
        self.remove_plugin(plugin_id);
        log::debug!("Invalidated cache for plugin update: {}", plugin_id);
    }

    /// Log cache statistics (useful for debugging and monitoring)
    pub fn log_stats(&self) {
        let stats = self.get_stats();
        log::debug!("{}", stats);
    }

    /// Get cache statistics
    pub fn get_stats(&self) -> CacheStats {
        let plugin_cache = self.plugin_cache.lock().unwrap();
        let dataset_cache = self.dataset_cache.lock().unwrap();
        let plugin_datasets_cache = self.plugin_datasets_cache.lock().unwrap();

        CacheStats {
            plugin_cache_len: plugin_cache.len(),
            plugin_cache_capacity: plugin_cache.cap().get(),
            dataset_cache_len: dataset_cache.len(),
            dataset_cache_capacity: dataset_cache.cap().get(),
            plugin_datasets_cache_len: plugin_datasets_cache.len(),
            plugin_datasets_cache_capacity: plugin_datasets_cache.cap().get(),
        }
    }
}

/// Cache statistics
#[derive(Debug, Clone)]
pub struct CacheStats {
    pub plugin_cache_len: usize,
    pub plugin_cache_capacity: usize,
    pub dataset_cache_len: usize,
    pub dataset_cache_capacity: usize,
    pub plugin_datasets_cache_len: usize,
    pub plugin_datasets_cache_capacity: usize,
}

impl std::fmt::Display for CacheStats {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "Cache Stats:\n  Plugins: {}/{}\n  Datasets: {}/{}\n  Plugin Datasets: {}/{}",
            self.plugin_cache_len,
            self.plugin_cache_capacity,
            self.dataset_cache_len,
            self.dataset_cache_capacity,
            self.plugin_datasets_cache_len,
            self.plugin_datasets_cache_capacity
        )
    }
}
