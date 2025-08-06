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

use diesel::SqliteConnection;

use crate::{database::dataset_dao, repository::model::dataset};

pub(crate) struct DatasetRepository {
    sqlite_connection: SqliteConnection,
}

impl DatasetRepository {
    pub(crate) fn new(sqlite_connection: SqliteConnection) -> DatasetRepository {
        DatasetRepository { sqlite_connection }
    }

    pub(crate) fn save_dataset(&mut self, plugin_id: &str, dataset: Dataset) -> Result<(), String> {
        let entity = dataset::dataset_to_entity(plugin_id.to_string(), dataset)?;
        dataset_dao::upsert(&mut self.sqlite_connection, &entity).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub(crate) fn save_datasets(
        &mut self,
        plugin_id: &str,
        datasets: Vec<Dataset>,
    ) -> Result<(), String> {
        let entities = dataset::datasets_to_entities(plugin_id.to_string(), datasets)?;
        dataset_dao::upsert_all(&mut self.sqlite_connection, &entities)
            .map_err(|e| e.to_string())?;
        Ok(())
    }

    pub(crate) fn get_datasets(&mut self, plugin_id: &str) -> Result<Vec<Dataset>, String> {
        let entities = dataset_dao::find_by_plugin_id(&mut self.sqlite_connection, plugin_id)
            .map_err(|e| e.to_string())?;
        Ok(dataset::datasets_entities_to_external_models(entities)?)
    }

    pub(crate) fn get_dataset(&mut self, id: &str) -> Result<Dataset, String> {
        let entity =
            dataset_dao::find_by_id(&mut self.sqlite_connection, id).map_err(|e| e.to_string())?;
        Ok(dataset::dataset_entity_to_external_model(entity)?)
    }

    pub(crate) fn delete_dataset(&mut self, id: &str) -> Result<(), String> {
        dataset_dao::delete_by_id(&mut self.sqlite_connection, id).map_err(|e| e.to_string())?;
        Ok(())
    }

    pub(crate) fn delete_datasets(&mut self, plugin_id: &str) -> Result<(), String> {
        dataset_dao::delete_by_plugin_id(&mut self.sqlite_connection, plugin_id)
            .map_err(|e| e.to_string())?;
        Ok(())
    }
}
