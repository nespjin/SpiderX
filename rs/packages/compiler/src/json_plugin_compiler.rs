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

use core::data::{
    json::plugin_manifest_json_data::PluginManifestJsonData,
    plugin::{Dataset, Plugin},
    screen_type::ScreenType,
};
use std::string::String;

use serde_json::Error;

use crate::plugin_compiler::PluginCompiler;

pub struct JsonPluginCompiler {
    plugin_manifest_json_data: PluginManifestJsonData,
}

impl JsonPluginCompiler {
    pub fn parse(json_string: &str) -> Result<JsonPluginCompiler, Error> {
        let json_data = PluginManifestJsonData::parse(json_string)?;
        Ok(JsonPluginCompiler::from_json_data(json_data))
    }

    pub fn from_json_data(plugin_manifest_json_data: PluginManifestJsonData) -> Self {
        JsonPluginCompiler {
            plugin_manifest_json_data,
        }
    }
}

impl PluginCompiler for JsonPluginCompiler {
    fn compile(&self) -> Result<Plugin, String> {
        let json_data = &self.plugin_manifest_json_data;

        let datasets = json_data
            .dataset
            .iter()
            .map(|json_dataset| Dataset {
                id: json_dataset.id.clone(),
                url: json_dataset.url.clone(),
                url_compact: json_dataset.url_compact.clone(),
                url_medium: json_dataset.url_medium.clone(),
                url_expanded: json_dataset.url_expanded.clone(),
                js: json_dataset.js.clone(),
                js_compact: json_dataset.js_compact.clone(),
                js_medium: json_dataset.js_medium.clone(),
                js_expanded: json_dataset.js_expanded.clone(),
                dsl: json_dataset.dsl.clone(),
                dsl_compact: json_dataset.dsl_compact.clone(),
                dsl_medium: json_dataset.dsl_medium.clone(),
                dsl_expanded: json_dataset.dsl_expanded.clone(),
            })
            .collect::<Vec<_>>();

        let screen_type_results = json_data
            .supported_screen_types
            .iter()
            .map(|e| ScreenType::from_str(e));

        let mut screen_types: Vec<ScreenType> = vec![];
        for type_result in screen_type_results {
            screen_types.push(type_result?);
        }

        Ok(Plugin {
            id: json_data.id.clone(),
            name: json_data.name.clone(),
            author: json_data.author.clone(),
            version: json_data.version.clone(),
            runtime_version: json_data.runtime_version.clone(),
            description: json_data.description.clone(),
            tags: json_data.tags.clone(),
            supported_screen_types: screen_types,
            datasets: datasets,
        })
    }
}
