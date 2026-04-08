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

use spiderx_core::data::plugin::Dataset;
use std::collections::HashMap;

use crate::{
    cache::CacheManager,
    database::{connection_pool::get_pooled_connection, dataset_dao},
    device::device_manager::DeviceManager,
    executor::{
        dataset_executor::DatasetExecutor, javascript_dataset_executor::JavaScriptDatasetExecutor,
        request_dataset_listener::RequestDatasetListenerWrpper,
    },
    plugin_manager::RequestType,
    repository::{model::dataset, request_dataset_options::RequestDatasetOptions},
    utils::{screen_typed_value::ScreenTypedValue, url_utils},
};

macro_rules! new_javascript_executor {
    ($dataset_id:expr, $url_str:expr, $js:expr, $req_timeout:expr, $js_dataset_listener:expr, $config:expr) => {{
        let mut executor = JavaScriptDatasetExecutor::new($dataset_id, $url_str, $js);
        executor
            .with_timeout($req_timeout)
            .with_opt_listener($js_dataset_listener)
            .with_opt_config($config);
        Box::new(executor)
    }};
}

macro_rules! new_option_javascript_executor {
    ($dataset_id:expr, $url_str:expr, $js:expr, $req_timeout:expr, $js_dataset_listener:expr, $config:expr) => {
        $js.as_ref().map(|js| {
            let mut executor = JavaScriptDatasetExecutor::new($dataset_id, $url_str, js);
            executor
                .with_timeout($req_timeout)
                .with_opt_listener($js_dataset_listener)
                .with_opt_config($config);
            Box::new(executor)
        })
    };
}

macro_rules! new_auto_executor {
    ($dataset_id:expr, $url_str:expr, $dsl:expr, $js:expr, $req_timeout:expr, $js_dataset_listener:expr, $config:expr) => {{
        if let Some(_) = $dsl {
            // Box::new(DslDatasetExecutor::new(dataset_id, url_value, &dsl))
            // TODO: Remove this
            new_javascript_executor!(
                $dataset_id,
                $url_str,
                "",
                $req_timeout,
                $js_dataset_listener,
                $config
            )
        } else if let Some(js) = $js {
            new_javascript_executor!(
                $dataset_id,
                $url_str,
                js,
                $req_timeout,
                $js_dataset_listener,
                $config
            )
        } else {
            return Err("The dsl and js is both empty".to_string());
        }
    }};
}

pub struct DatasetRepository {}

impl DatasetRepository {
    pub fn new() -> DatasetRepository {
        DatasetRepository {}
    }

    pub fn save_dataset(&self, plugin_id: &str, dataset: Dataset) -> Result<(), String> {
        let cache = CacheManager::init();

        // Invalidate old cache entries
        cache.remove_dataset(plugin_id, &dataset.id);
        cache.remove_plugin_datasets(plugin_id); // Invalidate list cache

        let mut pooled_conn = get_pooled_connection()?;
        let entity = dataset::dataset_to_entity(plugin_id.to_string(), dataset.clone())?;
        dataset_dao::upsert(&mut *pooled_conn, &entity).map_err(|e| e.to_string())?;

        // Update cache with new data
        cache.insert_dataset(plugin_id.to_string(), dataset.id.clone(), dataset.clone());
        log::debug!("Updated cache for dataset: {}.{}", plugin_id, dataset.id);

        Ok(())
    }

    pub fn save_datasets(&self, plugin_id: &str, datasets: Vec<Dataset>) -> Result<(), String> {
        let cache = CacheManager::init();

        // Invalidate old cache entries for this plugin
        cache.remove_plugin_datasets(plugin_id);

        let mut pooled_conn = get_pooled_connection()?;
        let entities = dataset::datasets_to_entities(plugin_id.to_string(), datasets.clone())?;
        dataset_dao::upsert_all(&mut *pooled_conn, &entities)
            .map(|_| ())
            .map_err(|e| e.to_string())?;

        // Update cache for all datasets
        for dataset in &datasets {
            cache.insert_dataset(plugin_id.to_string(), dataset.id.clone(), dataset.clone());
        }
        // Note: We don't cache the list here - it will be cached on first read

        log::debug!(
            "Updated cache for {} datasets of plugin: {}",
            datasets.len(),
            plugin_id
        );
        Ok(())
    }

    pub fn get_datasets(&self, plugin_id: &str) -> Result<Vec<Dataset>, String> {
        let cache = CacheManager::init();

        // Check cache first
        if let Some(cached) = cache.get_plugin_datasets(plugin_id) {
            log::debug!("Cache hit for plugin datasets: {}", plugin_id);
            return Ok(cached);
        }

        log::debug!(
            "Cache miss for plugin datasets: {}, querying database",
            plugin_id
        );

        let mut pooled_conn = get_pooled_connection()?;
        let entities = dataset_dao::find_by_plugin_id(&mut *pooled_conn, plugin_id)
            .map_err(|e| e.to_string())?;
        let datasets = dataset::datasets_entities_to_external_models(entities)?;

        // Cache the result
        cache.insert_plugin_datasets(plugin_id.to_string(), datasets.clone());
        log::debug!(
            "Cached {} datasets for plugin: {}",
            datasets.len(),
            plugin_id
        );

        Ok(datasets)
    }

    pub fn get_dataset(&self, id: &str) -> Result<Option<Dataset>, String> {
        // Note: This method doesn't know the plugin_id, so we can't efficiently cache it
        // We'll use pooled connection but skip caching for this method
        let mut pooled_conn = get_pooled_connection()?;

        match dataset_dao::find_by_id(&mut *pooled_conn, id).map_err(|e| e.to_string())? {
            Some(entity) => Ok(Some(dataset::dataset_entity_to_external_model(entity)?)),
            None => Ok(None),
        }
    }

    pub fn get_dataset_in_plugin(
        &self,
        plugin_id: &str,
        id: &str,
    ) -> Result<Option<Dataset>, String> {
        if plugin_id.is_empty() {
            return Err("Plugin id is empty".to_string());
        }

        if id.is_empty() {
            return Err("Dataset id is empty".to_string());
        }

        let cache = CacheManager::init();

        // Check cache first
        if let Some(cached) = cache.get_dataset(plugin_id, id) {
            log::debug!("Cache hit for dataset: {}.{}", plugin_id, id);
            return Ok(Some(cached));
        }

        log::debug!("Cache miss for dataset: {}.{}", plugin_id, id);

        let mut pooled_conn = get_pooled_connection()?;
        match dataset_dao::find_by_id_in_plugin(&mut *pooled_conn, plugin_id, id)
            .map_err(|e| e.to_string())?
        {
            Some(entity) => {
                let dataset = dataset::dataset_entity_to_external_model(entity)?;

                // Cache the result
                cache.insert_dataset(plugin_id.to_string(), id.to_string(), dataset.clone());
                log::debug!("Cached dataset: {}.{}", plugin_id, id);

                Ok(Some(dataset))
            }
            None => Ok(None),
        }
    }

    pub fn delete_dataset(&self, id: &str) -> Result<(), String> {
        // First get the dataset from database to know plugin_id for precise cache invalidation
        let mut pooled_conn = get_pooled_connection()?;

        let entity = dataset_dao::find_by_id(&mut *pooled_conn, id).map_err(|e| e.to_string())?;

        if let Some(entity) = entity {
            let plugin_id = entity.plugin_id.clone();
            let dataset_id = entity.id.clone();

            // Delete from database
            dataset_dao::delete_by_id(&mut *pooled_conn, id)
                .map(|_| ())
                .map_err(|e| e.to_string())?;

            // Precise cache invalidation
            let cache = CacheManager::init();
            cache.remove_dataset(&plugin_id, &dataset_id);
            cache.remove_plugin_datasets(&plugin_id); // Invalidate list cache

            log::debug!(
                "Invalidated cache for deleted dataset: {}.{}",
                plugin_id,
                dataset_id
            );
        } else {
            log::warn!("Attempted to delete non-existent dataset: {}", id);
        }

        Ok(())
    }

    pub fn delete_datasets(&self, plugin_id: &str) -> Result<(), String> {
        let mut pooled_conn = get_pooled_connection()?;
        dataset_dao::delete_by_plugin_id(&mut *pooled_conn, plugin_id)
            .map(|_| ())
            .map_err(|e| e.to_string())?;

        // Invalidate cache for this plugin's datasets
        let cache = CacheManager::init();
        cache.remove_plugin_datasets(plugin_id);
        log::debug!(
            "Invalidated cache for deleted datasets of plugin: {}",
            plugin_id
        );

        Ok(())
    }

    pub fn request_dataset(
        &self,
        plugin_id: &str,
        dataset_id: &str,
        options: RequestDatasetOptions,
    ) -> Result<String, String> {
        let dataset = self.get_dataset_in_plugin(plugin_id, dataset_id)?;
        let dataset = match dataset {
            Some(dataset) => dataset,
            None => return Err(format!("Dataset {}.{} not found", plugin_id, dataset_id)),
        };

        let RequestDatasetOptions {
            timeout: req_timeout,
            url: url_override,
            listener,
            config,
            req_type,
            url_placeholders,
            // ..
        } = options;

        let screen_type = {
            let dm = DeviceManager::get_instance();
            &dm.screen_type().ok_or("Screen type is not set")?
        };

        let url_str = url_override.or_else(|| {
            ScreenTypedValue::new()
                .with_value(dataset.url.clone())
                .with_option_compact(dataset.url_compact.clone())
                .with_option_medium(dataset.url_medium.clone())
                .with_option_expanded(dataset.url_expanded.clone())
                .into_value(screen_type)
        });

        if url_str.is_none() {
            return Err("The url is empty".to_string());
        };
        let url_str = url_str.expect("The url is empty");
        let url_str = &if let Some(placeholders) = url_placeholders {
            url_utils::url_replace_placeholders(&url_str, &placeholders)
        } else {
            url_str
        };

        let js = ScreenTypedValue::new()
            .with_option_value(dataset.js.clone())
            .with_option_compact(dataset.js_compact.clone())
            .with_option_medium(dataset.js_medium.clone())
            .with_option_expanded(dataset.js_expanded.clone())
            .into_value(screen_type);

        let dsl = ScreenTypedValue::new()
            .with_option_value(dataset.dsl.clone())
            .with_option_compact(dataset.dsl_compact.clone())
            .with_option_medium(dataset.dsl_medium.clone())
            .with_option_expanded(dataset.dsl_expanded.clone())
            .into_value(screen_type)
            .map(|e| e.clone())
            .map(|e| serde_json::from_value::<HashMap<String, serde_json::Value>>(e))
            .map(|e| e.ok())
            .flatten();

        let js_dataset_listener = listener
            .map(|e| match e {
                RequestDatasetListenerWrpper::JavaScriptDataset(l) => Some(l.clone()),
                _ => None,
            })
            .flatten();

        let dataset_executor: Box<dyn DatasetExecutor> = match req_type {
            RequestType::Auto => {
                new_auto_executor!(
                    dataset_id,
                    url_str,
                    dsl,
                    &js,
                    req_timeout,
                    js_dataset_listener,
                    config
                )
            }
            RequestType::JavaScript => {
                let ret = new_option_javascript_executor!(
                    dataset_id,
                    url_str,
                    js,
                    req_timeout,
                    js_dataset_listener,
                    config
                );
                match ret {
                    Some(executor) => executor,
                    None => {
                        return Err("Request is set to JavaScript but the js is empty".to_string());
                    }
                }
            }
            RequestType::Dsl => {
                new_javascript_executor!(
                    dataset_id,
                    url_str,
                    "",
                    req_timeout,
                    js_dataset_listener,
                    config
                )
            }
        };

        let result = dataset_executor.request();

        log::debug!(
            "DatasetRepository::request_dataset {} {} {:?}",
            plugin_id,
            dataset_id,
            result
        );

        result
    }

    pub fn auto_request_type(
        &self,
        plugin_id: &str,
        dataset_id: &str,
    ) -> Result<RequestType, String> {
        let dataset = self.get_dataset_in_plugin(plugin_id, dataset_id)?;
        let dataset = match dataset {
            Some(dataset) => dataset,
            None => return Err(format!("Dataset {}.{} not found", plugin_id, dataset_id)),
        };

        let screen_type = {
            let dm = DeviceManager::get_instance();
            &dm.screen_type().ok_or("Screen type is not set")?
        };

        let dsl = ScreenTypedValue::new()
            .with_option_value(dataset.dsl.clone())
            .with_option_compact(dataset.dsl_compact.clone())
            .with_option_medium(dataset.dsl_medium.clone())
            .with_option_expanded(dataset.dsl_expanded.clone());
        let dsl_value = dsl.value(screen_type);

        match dsl_value {
            Some(_) => Ok(RequestType::Dsl),
            None => Ok(RequestType::JavaScript),
        }
    }
}
