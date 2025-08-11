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

use core::data::plugin::Dataset;
use std::collections::HashMap;

use crate::{
    database::{database, dataset_dao},
    device::device_manager::DeviceManager,
    executor::{
        dataset_executor::DatasetExecutor,
        javascript_dataset_executor::JavaScriptDatasetExecutor,
    },
    repository::model::dataset,
    utils::screen_typed_value::ScreenTypedValue,
};

pub struct DatasetRepository {
    database_path: String,
}

impl DatasetRepository {
    pub fn new(database_path: String) -> DatasetRepository {
        DatasetRepository { database_path }
    }

    pub fn save_dataset(&self, plugin_id: &str, dataset: Dataset) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entity = dataset::dataset_to_entity(plugin_id.to_string(), dataset)?;
        dataset_dao::upsert(&mut sqlite_connection, &entity).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub fn save_datasets(&self, plugin_id: &str, datasets: Vec<Dataset>) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entities = dataset::datasets_to_entities(plugin_id.to_string(), datasets)?;
        dataset_dao::upsert_all(&mut sqlite_connection, &entities).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub fn get_datasets(&self, plugin_id: &str) -> Result<Vec<Dataset>, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entities = dataset_dao::find_by_plugin_id(&mut sqlite_connection, plugin_id)
            .map_err(|e| e.to_string())?;
        Ok(dataset::datasets_entities_to_external_models(entities)?)
    }

    pub fn get_dataset(&self, id: &str) -> Result<Option<Dataset>, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;

        match dataset_dao::find_by_id(&mut sqlite_connection, id).map_err(|e| e.to_string())? {
            Some(entity) => Ok(Some(dataset::dataset_entity_to_external_model(entity)?)),
            None => Ok(None),
        }
    }

    pub fn get_dataset_in_plugin(
        &self,
        plugin_id: &str,
        id: &str,
    ) -> Result<Option<Dataset>, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        match dataset_dao::find_by_id_in_plugin(&mut sqlite_connection, plugin_id, id)
            .map_err(|e| e.to_string())?
        {
            Some(entity) => Ok(Some(dataset::dataset_entity_to_external_model(entity)?)),
            None => Ok(None),
        }
    }

    pub fn delete_dataset(&self, id: &str) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        dataset_dao::delete_by_id(&mut sqlite_connection, id).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub fn delete_datasets(&self, plugin_id: &str) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        dataset_dao::delete_by_plugin_id(&mut sqlite_connection, plugin_id)
            .map_err(|e| e.to_string())?;
        Ok(())
    }

    pub fn request_dataset(&self, plugin_id: &str, dataset_id: &str) -> Result<String, String> {
        let dataset = self.get_dataset_in_plugin(plugin_id, dataset_id)?;
        let dataset = match dataset {
            Some(dataset) => dataset,
            None => return Err(format!("Dataset {}.{} not found", plugin_id, dataset_id)),
        };

        let dm = DeviceManager::get_instance();
        let dm = dm.lock().map_err(|e| e.to_string())?;

        let screen_type = &dm.screen_type().ok_or("Screen type is not set")?;

        let url = ScreenTypedValue::new()
            .with_value(dataset.url.clone())
            .with_option_compact(dataset.url_compact.clone())
            .with_option_medium(dataset.url_medium.clone())
            .with_option_expanded(dataset.url_expanded.clone());
        let url_value = if let Some(url) = url.value(screen_type) {
            url
        } else {
            return Err("The url is empty".to_string());
        };

        let js = ScreenTypedValue::new()
            .with_option_value(dataset.js.clone())
            .with_option_compact(dataset.js_compact.clone())
            .with_option_medium(dataset.js_medium.clone())
            .with_option_expanded(dataset.js_expanded.clone());
        let js_value = js.value(screen_type);

        let dsl = ScreenTypedValue::new()
            .with_option_value(dataset.dsl.clone())
            .with_option_compact(dataset.dsl_compact.clone())
            .with_option_medium(dataset.dsl_medium.clone())
            .with_option_expanded(dataset.dsl_expanded.clone());
        let dsl_value = dsl.value(screen_type);

        let dsl = if let Some(dsl) = dsl_value {
            let dsl_map: HashMap<String, serde_json::Value> =
                serde_json::from_value(dsl.clone()).map_err(|e| e.to_string())?;
            dsl_map
        } else {
            HashMap::new()
        };

        let dataset_ds: Box<dyn DatasetExecutor> = if let Some(_) = dsl_value {
            // Box::new(DslDatasetExecutor::new(dataset_id, url_value, &dsl))
            // TODO: Remove this
            Box::new(JavaScriptDatasetExecutor::new(
                dataset_id,
                url_value,
                js_value.unwrap(),
            ))
        } else if let Some(js) = js_value {
            Box::new(JavaScriptDatasetExecutor::new(dataset_id, url_value, js))
        } else {
            return Err("The dsl and js is both empty".to_string());
        };

        let result = dataset_ds.request();

        Ok(result?)
    }
}
