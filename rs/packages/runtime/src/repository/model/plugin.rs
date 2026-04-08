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

use spiderx_core::data::{plugin::Plugin, screen_type::ScreenType};

use crate::{
    database::entities::{dataset::DatasetEntity, plugin::PluginEntity},
    repository::model::dataset::datasets_entities_to_external_models,
};

pub(crate) fn plugins_to_entities(plugins: Vec<Plugin>) -> Result<Vec<PluginEntity>, String> {
    let mut entities: Vec<PluginEntity> = Vec::new();
    for plugin in plugins {
        entities.push(plugin_to_entity(plugin)?);
    }
    Ok(entities)
}

pub(crate) fn plugin_to_entity(plugin: Plugin) -> Result<PluginEntity, String> {
    Ok(PluginEntity {
        id: plugin.id,
        name: plugin.name,
        author: plugin.author,
        version: plugin.version,
        runtime_version: plugin.runtime_version,
        description: plugin.description,
        tags: if plugin.tags.is_empty() {
            None
        } else {
            Some(plugin.tags.join(","))
        },
        supported_screen_types: if plugin.supported_screen_types.is_empty() {
            None
        } else {
            Some(
                plugin
                    .supported_screen_types
                    .iter()
                    .map(|e| e.as_str())
                    .collect::<Vec<&str>>()
                    .join(","),
            )
        },
    })
}

pub(crate) fn entities_to_external_models(
    entities: Vec<PluginEntity>,
    datasets: Vec<Vec<DatasetEntity>>,
) -> Result<Vec<Plugin>, String> {
    if entities.len() != datasets.len() {
        return Err("entities_to_external_models entities.len() != datasets.len()".to_string());
    }

    let mut models: Vec<Plugin> = Vec::new();
    let mut i = 0;
    for entity in entities {
        models.push(entity_to_external_model(entity, datasets[i].clone())?);
        i += 1;
    }

    Ok(models)
}

pub(crate) fn entity_to_external_model(
    entity: PluginEntity,
    dataset_entities: Vec<DatasetEntity>,
) -> Result<Plugin, String> {
    Ok(Plugin {
        id: entity.id,
        name: entity.name,
        author: entity.author,
        version: entity.version,
        runtime_version: entity.runtime_version,
        description: entity.description,
        tags: entity
            .tags
            .map(|e| e.split(",").map(|e| e.to_string()).collect::<Vec<_>>())
            .unwrap_or(vec![]),
        supported_screen_types: entity
            .supported_screen_types
            .map(|e| {
                e.split(",")
                    .map(|e| ScreenType::from_str(e).unwrap())
                    .collect::<Vec<_>>()
            })
            .unwrap_or(vec![]),
        datasets: datasets_entities_to_external_models(dataset_entities)?,
    })
}
