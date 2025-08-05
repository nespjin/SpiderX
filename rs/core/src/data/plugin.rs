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

use std::collections::HashMap;

use crate::data::screen_type::ScreenType;

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
    pub dsl: Option<HashMap<String, String>>,
    pub dsl_compact: Option<HashMap<String, String>>,
    pub dsl_medium: Option<HashMap<String, String>>,
    pub dsl_expanded: Option<HashMap<String, String>>,
}

/// The struct that represents a plugin.
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
