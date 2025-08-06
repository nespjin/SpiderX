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

use serde::{Deserialize, Serialize};
use serde_json::Error;

#[derive(Clone, Debug, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct DatasetJsonData {
    /// The id of current dataset.
    ///
    /// The id must be unique in the same plugin.
    pub id: String,

    /// The url will be used to request data.
    pub url: String,

    /// The url for `Compact` screen type.
    #[serde(rename = "url@compact")]
    pub url_compact: Option<String>,

    /// The url for `Medium` screen type.
    #[serde(rename = "url@medium")]
    pub url_medium: Option<String>,

    /// The url for `Expanded` screen type.
    #[serde(rename = "url@expanded")]
    pub url_expanded: Option<String>,

    /// The javascript code that will be executed when the data is requested.
    pub js: Option<String>,

    /// The js for `Compact` screen type.
    #[serde(rename = "js@compact")]
    pub js_compact: Option<String>,

    /// The js for `Medium` screen type.
    #[serde(rename = "js@medium")]
    pub js_medium: Option<String>,

    /// The js for `Expanded` screen type.
    #[serde(rename = "js@expanded")]
    pub js_expanded: Option<String>,

    /// The dsl code that will be executed when the data is requested.
    pub dsl: Option<serde_json::Value>,

    /// The dsl for `Compact` screen type.
    #[serde(rename = "dsl@compact")]
    pub dsl_compact: Option<serde_json::Value>,

    /// The dsl for `Medium` screen type.
    #[serde(rename = "dsl@medium")]
    pub dsl_medium: Option<serde_json::Value>,

    /// The dsl for `Expanded` screen type.
    #[serde(rename = "dsl@expanded")]
    pub dsl_expanded: Option<serde_json::Value>,
}

#[derive(Debug, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct PluginManifestJsonData {
    /// The parent of current plugin.
    ///
    /// It can be a Json object or a string.
    ///
    /// If it is a string, it means the parent plugin's remote url or local path.
    ///
    /// If it is a Json object, it means the parent plugin's manifest.
    pub parent: Option<serde_json::Value>,

    /// The plugin id.
    ///
    /// It must be unique in the plugin system.
    pub id: String,

    /// The plugin name.
    ///
    /// It is used to display in the UI.
    pub name: String,

    /// The plugin author.
    pub author: Option<String>,

    /// The plugin version.
    ///
    /// The version must format as [semver2.0](https://semver.org/)
    pub version: String,

    /// The plugin runtime version compatible with.
    ///
    /// The version must format as [semver2.0](https://semver.org/)
    pub runtime_version: String,

    /// The plugin description.
    pub description: Option<String>,

    /// The plugin tags.
    pub tags: Vec<String>,

    /// The plugin supported screen types.
    ///
    /// The screen types must be one of the following:
    /// - `compact`: Compact screen such as Mobile phone.
    /// - `medium`: Medium screen such as Tablet.
    /// - `expanded`: Expanded screen such as Desktop.
    pub supported_screen_types: Vec<String>,

    /// The plugin variables.
    pub variables: serde_json::Value,

    /// The plugin dataset.
    pub dataset: Vec<DatasetJsonData>,
}

impl PluginManifestJsonData {
    pub fn parse(json_string: String) -> Result<PluginManifestJsonData, Error> {
        serde_json::from_str(&json_string)
    }

    pub fn to_json(&self) -> Result<String, Error> {
        serde_json::to_string_pretty(self)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_plugin_manifest_creation() {
        let manifest = PluginManifestJsonData {
            parent: None,
            id: "test_id".to_string(),
            name: "Test Plugin".to_string(),
            author: Some("Test Author".to_string()),
            version: "1.0.0".to_string(),
            runtime_version: "1.0".to_string(),
            description: Some("A test plugin".to_string()),
            tags: vec!["test".to_string(), "example".to_string()],
            supported_screen_types: vec!["mobile".to_string(), "tablet".to_string()],
            variables: serde_json::json!({}),
            dataset: vec![DatasetJsonData {
                id: "test_dataset".to_string(),
                url: "https://api.example.com/data".to_string(),
                url_compact: None,
                url_medium: None,
                url_expanded: None,
                js: None,
                js_compact: None,
                js_medium: None,
                js_expanded: None,
                dsl: None,
                dsl_compact: None,
                dsl_medium: None,
                dsl_expanded: None,
            }],
        };

        assert_eq!(manifest.id, "test_id");
        assert_eq!(manifest.name, "Test Plugin");
        assert_eq!(manifest.version, "1.0.0");
        assert_eq!(manifest.runtime_version, "1.0");
    }

    #[test]
    fn test_plugin_manifest_serialization() {
        let manifest = PluginManifestJsonData {
            parent: None,
            id: "test_id".to_string(),
            name: "Test Plugin".to_string(),
            author: Some("Test Author".to_string()),
            version: "1.0.0".to_string(),
            runtime_version: "1.0".to_string(),
            description: Some("A test plugin".to_string()),
            tags: vec!["test".to_string()],
            supported_screen_types: vec!["mobile".to_string()],
            variables: serde_json::json!({
                "key1": "value1",
                "key2": 123
            }),
            dataset: vec![DatasetJsonData {
                id: "data1".to_string(),
                url: "https://api.example.com/data1".to_string(),
                url_compact: None,
                url_medium: None,
                url_expanded: None,
                js: Some("function process(data) { return data; }".to_string()),
                js_compact: None,
                js_medium: None,
                js_expanded: None,
                dsl: Some(serde_json::json!({
                    "type": "json"
                })),
                dsl_compact: None,
                dsl_medium: None,
                dsl_expanded: None,
            }],
        };

        let json_string = serde_json::to_string_pretty(&manifest).unwrap();
        let deserialized_manifest: PluginManifestJsonData =
            serde_json::from_str(&json_string).unwrap();

        assert_eq!(manifest.id, deserialized_manifest.id);
        assert_eq!(manifest.name, deserialized_manifest.name);
        assert_eq!(manifest.version, deserialized_manifest.version);
        assert_eq!(
            manifest.runtime_version,
            deserialized_manifest.runtime_version
        );
    }

    #[test]
    fn test_plugin_manifest_parent_field() {
        // Test with string parent
        let manifest_with_string_parent = PluginManifestJsonData {
            parent: Some(serde_json::json!("https://example.com/parent-plugin.json")),
            id: "test_id".to_string(),
            name: "Test Plugin".to_string(),
            author: Some("Test Author".to_string()),
            version: "1.0.0".to_string(),
            runtime_version: "1.0".to_string(),
            description: Some("A test plugin".to_string()),
            tags: vec![],
            supported_screen_types: vec![],
            variables: serde_json::json!({}),
            dataset: vec![],
        };

        let json_string = serde_json::to_string_pretty(&manifest_with_string_parent).unwrap();
        let deserialized: PluginManifestJsonData = serde_json::from_str(&json_string).unwrap();

        assert_eq!(manifest_with_string_parent.parent, deserialized.parent);

        // Test with object parent
        let manifest_with_object_parent = PluginManifestJsonData {
            parent: Some(serde_json::json!({
                "id": "parent_id",
                "name": "Parent Plugin",
                "version": "1.0.0"
            })),
            id: "test_id".to_string(),
            name: "Test Plugin".to_string(),
            author: Some("Test Author".to_string()),
            version: "1.0.0".to_string(),
            runtime_version: "1.0".to_string(),
            description: Some("A test plugin".to_string()),
            tags: vec![],
            supported_screen_types: vec![],
            variables: serde_json::json!({}),
            dataset: vec![],
        };

        let json_string = serde_json::to_string_pretty(&manifest_with_object_parent).unwrap();
        let deserialized: PluginManifestJsonData = serde_json::from_str(&json_string).unwrap();

        assert_eq!(manifest_with_object_parent.parent, deserialized.parent);
    }

    #[test]
    fn test_plugin_manifest_variables_field() {
        let complex_variables = serde_json::json!({
            "string_var": "value1",
            "int_var": 42,
            "bool_var": true,
            "array_var": [1, 2, 3],
            "object_var": {
                "nested_key": "nested_value"
            }
        });

        let manifest = PluginManifestJsonData {
            parent: None,
            id: "test_id".to_string(),
            name: "Test Plugin".to_string(),
            author: Some("Test Author".to_string()),
            version: "1.0.0".to_string(),
            runtime_version: "1.0".to_string(),
            description: Some("A test plugin".to_string()),
            tags: vec![],
            supported_screen_types: vec![],
            variables: complex_variables.clone(),
            dataset: vec![],
        };

        let json_string = serde_json::to_string_pretty(&manifest).unwrap();
        let deserialized: PluginManifestJsonData = serde_json::from_str(&json_string).unwrap();

        assert_eq!(complex_variables, deserialized.variables);
    }

    #[test]
    fn test_plugin_manifest_dataset_field() {
        let complex_dataset = vec![
            DatasetJsonData {
                id: "user_data".to_string(),
                url: "https://api.example.com/users".to_string(),
                url_compact: Some("https://api.example.com/users/compact".to_string()),
                url_medium: Some("https://api.example.com/users/medium".to_string()),
                url_expanded: Some("https://api.example.com/users/expanded".to_string()),
                js: Some("function parse(data) { return JSON.parse(data); }".to_string()),
                js_compact: Some(
                    "function parse_compact(data) { return JSON.parse(data); }".to_string(),
                ),
                js_medium: Some(
                    "function parse_medium(data) { return JSON.parse(data); }".to_string(),
                ),
                js_expanded: Some(
                    "function parse_expanded(data) { return JSON.parse(data); }".to_string(),
                ),
                dsl: Some(serde_json::json!({
                    "parser": "json",
                    "fields": ["name", "age"]
                })),
                dsl_compact: Some(serde_json::json!({
                    "parser@compact": "json",
                    "fields@compact": ["name", "age"]
                })),
                dsl_medium: Some(serde_json::json!({
                    "parser@medium": "json",
                    "fields@medium": ["name", "age"]
                })),
                dsl_expanded: Some(serde_json::json!({
                    "parser@expanded": "json",
                    "fields@expanded": ["name", "age"]
                })),
            },
            DatasetJsonData {
                id: "product_data".to_string(),
                url: "https://api.example.com/products".to_string(),
                url_compact: None,
                url_medium: None,
                url_expanded: None,
                js: None,
                js_compact: None,
                js_medium: None,
                js_expanded: None,
                dsl: Some(serde_json::json!({
                    "parser": "json",
                    "fields": ["name", "price"]
                })),
                dsl_compact: None,
                dsl_medium: None,
                dsl_expanded: None,
            },
        ];

        let manifest = PluginManifestJsonData {
            parent: None,
            id: "test_id".to_string(),
            name: "Test Plugin".to_string(),
            author: Some("Test Author".to_string()),
            version: "1.0.0".to_string(),
            runtime_version: "1.0".to_string(),
            description: Some("A test plugin".to_string()),
            tags: vec![],
            supported_screen_types: vec![],
            variables: serde_json::json!({}),
            dataset: complex_dataset.clone(),
        };

        let json_string = serde_json::to_string_pretty(&manifest).unwrap();
        let deserialized: PluginManifestJsonData = serde_json::from_str(&json_string).unwrap();

        assert!(json_string.contains("url@compact"));
        assert!(json_string.contains("url@medium"));
        assert!(json_string.contains("url@expanded"));

        assert!(json_string.contains("js@compact"));
        assert!(json_string.contains("js@medium"));
        assert!(json_string.contains("js@expanded"));

        assert!(json_string.contains("dsl@compact"));
        assert!(json_string.contains("dsl@medium"));
        assert!(json_string.contains("dsl@expanded"));

        assert_eq!(complex_dataset.len(), deserialized.dataset.len());
        assert_eq!(complex_dataset[0].id, deserialized.dataset[0].id);
        assert_eq!(complex_dataset[0].url, deserialized.dataset[0].url);
        assert_eq!(
            complex_dataset[0].url_compact,
            deserialized.dataset[0].url_compact
        );
        assert_eq!(
            complex_dataset[0].url_medium,
            deserialized.dataset[0].url_medium
        );
        assert_eq!(
            complex_dataset[0].url_expanded,
            deserialized.dataset[0].url_expanded
        );
        assert_eq!(complex_dataset[0].js, deserialized.dataset[0].js);
        assert_eq!(
            complex_dataset[0].js_compact,
            deserialized.dataset[0].js_compact
        );
        assert_eq!(
            complex_dataset[0].js_medium,
            deserialized.dataset[0].js_medium
        );
        assert_eq!(
            complex_dataset[0].js_expanded,
            deserialized.dataset[0].js_expanded
        );
        assert_eq!(complex_dataset[0].dsl, deserialized.dataset[0].dsl);
        assert_eq!(
            complex_dataset[0].dsl_compact,
            deserialized.dataset[0].dsl_compact
        );
        assert_eq!(
            complex_dataset[0].dsl_medium,
            deserialized.dataset[0].dsl_medium
        );
        assert_eq!(
            complex_dataset[0].dsl_expanded,
            deserialized.dataset[0].dsl_expanded
        );
        assert_eq!(complex_dataset[1].id, deserialized.dataset[1].id);
        assert_eq!(complex_dataset[1].url, deserialized.dataset[1].url);
        assert_eq!(
            complex_dataset[1].url_compact,
            deserialized.dataset[1].url_compact
        );
        assert_eq!(
            complex_dataset[1].url_medium,
            deserialized.dataset[1].url_medium
        );
        assert_eq!(
            complex_dataset[1].url_expanded,
            deserialized.dataset[1].url_expanded
        );
        assert_eq!(complex_dataset[1].js, deserialized.dataset[1].js);
        assert_eq!(
            complex_dataset[1].js_compact,
            deserialized.dataset[1].js_compact
        );
        assert_eq!(
            complex_dataset[1].js_medium,
            deserialized.dataset[1].js_medium
        );
        assert_eq!(
            complex_dataset[1].js_expanded,
            deserialized.dataset[1].js_expanded
        );
        assert_eq!(complex_dataset[1].dsl, deserialized.dataset[1].dsl);
        assert_eq!(
            complex_dataset[1].dsl_compact,
            deserialized.dataset[1].dsl_compact
        );
        assert_eq!(
            complex_dataset[1].dsl_medium,
            deserialized.dataset[1].dsl_medium
        );
        assert_eq!(
            complex_dataset[1].dsl_expanded,
            deserialized.dataset[1].dsl_expanded
        );
    }
}
