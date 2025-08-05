/*
* Copyright (c) 2025. NESP Technology Corporation. All rights reserved.
*
* This program is not free software; you can't redistribute it and/or modify it
* without the permit of team manager.
*
* Unless required by applicable law or agreed to in writing.
*
* If you have any questions or if you find a bug,
* please contact the author by email or ask for Issues.
*/

use core::data::{
    json::plugin_manifest_json_data::PluginManifestJsonData,
    plugin::{Dataset, Plugin},
    screen_type::ScreenType,
};
use std::{collections::HashMap, string::String};

use serde_json::Error;

use crate::plugin_compiler::PluginCompiler;

pub struct JsonPluginCompiler {
    plugin_manifest_json_data: PluginManifestJsonData,
}

impl JsonPluginCompiler {
    pub fn parse(json_string: String) -> Result<JsonPluginCompiler, Error> {
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
                dsl: Some(HashMap::new()),
                dsl_compact: Some(HashMap::new()),
                dsl_medium: Some(HashMap::new()),
                dsl_expanded: Some(HashMap::new()),
            })
            .collect::<Vec<_>>();

        let screen_type_results = json_data
            .supported_screen_types
            .iter()
            .map(|e| ScreenType::from_string(e.to_string()));

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
