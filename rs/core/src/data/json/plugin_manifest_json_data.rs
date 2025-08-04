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

use serde::{Deserialize, Serialize};

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
    pub dataset: Vec<serde_json::Value>,
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
            dataset: vec![],
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
            dataset: vec![serde_json::json!({
                "name": "data1",
                "value": "value1"
            })],
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
}
