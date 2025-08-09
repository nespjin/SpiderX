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

use crate::{
    database::{database, dataset_dao},
    repository::model::dataset,
};

pub(crate) struct DatasetRepository {
    database_path: String,
}

impl DatasetRepository {
    pub(crate) fn new(database_path: String) -> DatasetRepository {
        DatasetRepository { database_path }
    }

    pub(crate) fn save_dataset(&self, plugin_id: &str, dataset: Dataset) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entity = dataset::dataset_to_entity(plugin_id.to_string(), dataset)?;
        dataset_dao::upsert(&mut sqlite_connection, &entity).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub(crate) fn save_datasets(
        &self,
        plugin_id: &str,
        datasets: Vec<Dataset>,
    ) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entities = dataset::datasets_to_entities(plugin_id.to_string(), datasets)?;
        dataset_dao::upsert_all(&mut sqlite_connection, &entities).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub(crate) fn get_datasets(&self, plugin_id: &str) -> Result<Vec<Dataset>, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        let entities = dataset_dao::find_by_plugin_id(&mut sqlite_connection, plugin_id)
            .map_err(|e| e.to_string())?;
        Ok(dataset::datasets_entities_to_external_models(entities)?)
    }

    pub(crate) fn get_dataset(&self, id: &str) -> Result<Option<Dataset>, String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;

        match dataset_dao::find_by_id(&mut sqlite_connection, id).map_err(|e| e.to_string())? {
            Some(entity) => Ok(Some(dataset::dataset_entity_to_external_model(entity)?)),
            None => Ok(None),
        }
    }

    pub(crate) fn delete_dataset(&self, id: &str) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        dataset_dao::delete_by_id(&mut sqlite_connection, id).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub(crate) fn delete_datasets(&self, plugin_id: &str) -> Result<(), String> {
        let mut sqlite_connection =
            database::open(&self.database_path).map_err(|e| e.to_string())?;
        dataset_dao::delete_by_plugin_id(&mut sqlite_connection, plugin_id)
            .map_err(|e| e.to_string())?;
        Ok(())
    }
}
