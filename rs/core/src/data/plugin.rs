use std::collections::HashSet;

use serde_json::Map;

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
use crate::data::screen_type::ScreenType;

pub struct Dataset {
    pub id: String,
    pub url: String,
    pub url_compact: String,
    pub url_medium: String,
    pub url_expanded: String,
    pub js: String,
    pub js_compact: String,
    pub js_medium: String,
    pub js_expanded: String,
    pub dsl: Map<String,String>,
    pub dsl_compact: Map<String,String>,
    pub dsl_medium: Map<String,String>,
    pub dsl_expanded: Map<String,String>,
}


/// The struct that represents a plugin.
pub struct Plugin {
    pub id: String,
    pub name: String,
    pub author: String,
    pub version: String,
    pub runtime_version: String,
    pub description: String,
    pub tags: Vec<String>,
    pub supported_screen_types: HashSet<ScreenType>,
    pub datasets: Vec<Dataset>,
}
