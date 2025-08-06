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

use crate::database::entities::dataset::DatasetEntity;

pub(crate) fn datasets_to_entities(
    plugin_id: String,
    datasets: Vec<Dataset>,
) -> Result<Vec<DatasetEntity>, String> {
    let mut entities: Vec<DatasetEntity> = Vec::new();
    for dataset in datasets {
        entities.push(dataset_to_entity(plugin_id.clone(), dataset)?);
    }

    Ok(entities)
}

pub(crate) fn dataset_to_entity(
    plugin_id: String,
    dataset: Dataset,
) -> Result<DatasetEntity, String> {
    let mut dsl_default: Option<String> = None;
    if let Some(dataset_dsl) = dataset.dsl {
        dsl_default = Some(serde_json::to_string(&dataset_dsl).map_err(|e| e.to_string())?);
    }

    let mut dsl_compact: Option<String> = None;
    if let Some(dataset_dsl_compact) = dataset.dsl_compact {
        dsl_compact = Some(serde_json::to_string(&dataset_dsl_compact).map_err(|e| e.to_string())?);
    }

    let mut dsl_medium: Option<String> = None;
    if let Some(dataset_dsl_medium) = dataset.dsl_medium {
        dsl_medium = Some(serde_json::to_string(&dataset_dsl_medium).map_err(|e| e.to_string())?);
    }

    let mut dsl_expanded: Option<String> = None;
    if let Some(dataset_dsl_expanded) = dataset.dsl_expanded {
        dsl_expanded =
            Some(serde_json::to_string(&dataset_dsl_expanded).map_err(|e| e.to_string())?);
    }

    Ok(DatasetEntity {
        id: dataset.id,
        plugin_id: plugin_id,
        url: dataset.url,
        url_compact: dataset.url_compact,
        url_medium: dataset.url_medium,
        url_expanded: dataset.url_expanded,
        js: dataset.js,
        js_compact: dataset.js_compact,
        js_medium: dataset.js_medium,
        js_expanded: dataset.js_expanded,
        dsl_default: dsl_default,
        dsl_compact: dsl_compact,
        dsl_medium: dsl_medium,
        dsl_expanded: dsl_expanded,
    })
}

pub(crate) fn datasets_entities_to_external_models(
    entities: Vec<DatasetEntity>,
) -> Result<Vec<Dataset>, String> {
    let mut models: Vec<Dataset> = Vec::new();
    for entity in entities {
        models.push(dataset_entity_to_external_model(entity)?);
    }

    Ok(models)
}

pub(crate) fn dataset_entity_to_external_model(entity: DatasetEntity) -> Result<Dataset, String> {
    let mut dsl: Option<serde_json::Value> = None;
    if let Some(entity_dsl) = entity.dsl_default {
        dsl = Some(serde_json::from_str(&entity_dsl).map_err(|e| e.to_string())?);
    }

    let mut dsl_compact: Option<serde_json::Value> = None;
    if let Some(entity_dsl_compact) = entity.dsl_compact {
        dsl_compact = Some(serde_json::from_str(&entity_dsl_compact).map_err(|e| e.to_string())?);
    }

    let mut dsl_medium: Option<serde_json::Value> = None;
    if let Some(entity_dsl_medium) = entity.dsl_medium {
        dsl_medium = Some(serde_json::from_str(&entity_dsl_medium).map_err(|e| e.to_string())?);
    }

    let mut dsl_expanded: Option<serde_json::Value> = None;
    if let Some(entity_dsl_expanded) = entity.dsl_expanded {
        dsl_expanded = Some(serde_json::from_str(&entity_dsl_expanded).map_err(|e| e.to_string())?);
    }

    Ok(Dataset {
        id: entity.id,
        url: entity.url,
        url_compact: entity.url_compact,
        url_medium: entity.url_medium,
        url_expanded: entity.url_expanded,
        js: entity.js,
        js_compact: entity.js_compact,
        js_medium: entity.js_medium,
        js_expanded: entity.js_expanded,
        dsl: dsl,
        dsl_compact: dsl_compact,
        dsl_medium: dsl_medium,
        dsl_expanded: dsl_expanded,
    })
}
