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

use crate::data::screen_type::ScreenType;

#[derive(Debug, Clone)]
pub struct Dataset {
    pub id: String,
    pub url: String,
    pub url_compact: Option<String>,
    pub url_medium: Option<String>,
    pub url_expanded: Option<String>,
    pub js: Option<String>,
    pub js_compact: Option<String>,
    pub js_medium: Option<String>,
    pub js_expanded: Option<String>,
    pub dsl: Option<serde_json::Value>,
    pub dsl_compact: Option<serde_json::Value>,
    pub dsl_medium: Option<serde_json::Value>,
    pub dsl_expanded: Option<serde_json::Value>,
    pub next_dataset_id: Option<String>,
}

/// The struct that represents a plugin.
#[derive(Debug, Clone)]
pub struct Plugin {
    pub id: String,
    pub name: String,
    pub author: Option<String>,
    pub version: String,
    pub runtime_version: String,
    pub description: Option<String>,
    pub tags: Vec<String>,
    pub supported_screen_types: Vec<ScreenType>,
    pub datasets: Vec<Dataset>,
}
