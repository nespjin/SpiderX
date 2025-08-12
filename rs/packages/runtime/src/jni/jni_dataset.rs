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

use jni::objects::{JObject, JValueGen};

use crate::jni::{
    jni_hander::JniHandler,
    jni_json::JniJson,
    jni_utils::{JniConstructorInfo, JniFieldInfo},
};

pub const JAVA_CLASS_NAME_DATASET: &'static str = "com/nesp/spiderx/runtime/model/Dataset";

pub const DATASET: &str = "com/nesp/spiderx/runtime/model/Dataset";
pub const DATASET_CONSTOR: JniConstructorInfo = (DATASET, "()V");

pub const FIELD_ID: JniFieldInfo = ("id", "Ljava/lang/String;");
pub const FIELD_URL: JniFieldInfo = ("url", "Ljava/lang/String;");
pub const FIELD_URL_COMPACT: JniFieldInfo = ("urlCompact", "Ljava/lang/String;");
pub const FIELD_URL_MEDIUM: JniFieldInfo = ("urlMedium", "Ljava/lang/String;");
pub const FIELD_URL_EXPANDED: JniFieldInfo = ("urlExpanded", "Ljava/lang/String;");
pub const FIELD_JS: JniFieldInfo = ("js", "Ljava/lang/String;");
pub const FIELD_JS_COMPACT: JniFieldInfo = ("jsCompact", "Ljava/lang/String;");
pub const FIELD_JS_MEDIUM: JniFieldInfo = ("jsMedium", "Ljava/lang/String;");
pub const FIELD_JS_EXPANDED: JniFieldInfo = ("jsExpanded", "Ljava/lang/String;");
pub const FIELD_DSL: JniFieldInfo = ("dsl", &"Ljava/util/Map;");
pub const FIELD_DSL_COMPACT: JniFieldInfo = ("dslCompact", "Ljava/util/Map;");
pub const FIELD_DSL_MEDIUM: JniFieldInfo = ("dslMedium", "Ljava/util/Map;");
pub const FIELD_DSL_EXPANDED: JniFieldInfo = ("dslExpanded", "Ljava/util/Map;");

pub struct JniDataset<'local> {
    handler: JniHandler<'local>,
    dataset: JObject<'local>,
    jni_json: JniJson<'local>,
}

impl<'local> JniDataset<'local> {
    pub fn new(handler: &mut JniHandler<'local>) -> Self {
        Self {
            handler: handler.clone(),
            dataset: handler.new_object(DATASET_CONSTOR, &[]),
            jni_json: JniJson::new(handler.clone()),
        }
    }

    pub fn jobject_ref(&self) -> &JObject<'local> {
        &self.dataset
    }

    pub fn unsafe_jobject(&self) -> JObject<'local> {
        unsafe { JObject::from_raw(*self.dataset) }
    }

    pub fn set_id(&mut self, id: &str) -> &mut Self {
        let id_obj = self.handler.new_string(id);
        self.handler
            .set_field(&self.dataset, FIELD_ID, JValueGen::Object(&id_obj));
        self
    }

    pub fn set_url(&mut self, url: &str) -> &mut Self {
        let url_obj = self.handler.new_string(url);
        self.handler
            .set_field(&self.dataset, FIELD_URL, JValueGen::Object(&url_obj));
        self
    }

    pub fn set_url_compact(&mut self, url: &str) -> &mut Self {
        let url_obj = self.handler.new_string(url);
        self.handler.set_field(
            &self.dataset,
            FIELD_URL_COMPACT,
            JValueGen::Object(&url_obj),
        );
        self
    }

    pub fn set_url_medium(&mut self, url: &str) -> &mut Self {
        let url_obj = self.handler.new_string(url);
        self.handler
            .set_field(&self.dataset, FIELD_URL_MEDIUM, JValueGen::Object(&url_obj));
        self
    }

    pub fn set_url_expanded(&mut self, url: &str) -> &mut Self {
        let url_obj = self.handler.new_string(url);
        self.handler.set_field(
            &self.dataset,
            FIELD_URL_EXPANDED,
            JValueGen::Object(&url_obj),
        );
        self
    }

    pub fn set_js(&mut self, js: &str) -> &mut Self {
        let js_obj = self.handler.new_string(js);
        self.handler
            .set_field(&self.dataset, FIELD_JS, JValueGen::Object(&js_obj));
        self
    }

    pub fn set_js_compact(&mut self, js: &str) -> &mut Self {
        let js_obj = self.handler.new_string(js);
        self.handler
            .set_field(&self.dataset, FIELD_JS_COMPACT, JValueGen::Object(&js_obj));
        self
    }

    pub fn set_js_medium(&mut self, js: &str) -> &mut Self {
        let js_obj = self.handler.new_string(js);
        self.handler
            .set_field(&self.dataset, FIELD_JS_MEDIUM, JValueGen::Object(&js_obj));
        self
    }

    pub fn set_js_expanded(&mut self, js: &str) -> &mut Self {
        let js_obj = self.handler.new_string(js);
        self.handler
            .set_field(&self.dataset, FIELD_JS_EXPANDED, JValueGen::Object(&js_obj));
        self
    }

    pub fn set_dsl(&mut self, dsl: &serde_json::Value) -> &mut Self {
        let dsl_obj = self.jni_json.json_value_to_hash_map(dsl);
        self.handler
            .set_field(&self.dataset, FIELD_DSL, JValueGen::Object(&dsl_obj));
        self
    }

    pub fn set_dsl_compact(&mut self, dsl: &serde_json::Value) -> &mut Self {
        let dsl_obj = self.jni_json.json_value_to_hash_map(dsl);
        self.handler.set_field(
            &self.dataset,
            FIELD_DSL_COMPACT,
            JValueGen::Object(&dsl_obj),
        );
        self
    }

    pub fn set_dsl_medium(&mut self, dsl: &serde_json::Value) -> &mut Self {
        let dsl_obj = self.jni_json.json_value_to_hash_map(dsl);
        self.handler
            .set_field(&self.dataset, FIELD_DSL_MEDIUM, JValueGen::Object(&dsl_obj));
        self
    }

    pub fn set_dsl_expanded(&mut self, dsl: &serde_json::Value) -> &mut Self {
        let dsl_obj = self.jni_json.json_value_to_hash_map(dsl);
        self.handler.set_field(
            &self.dataset,
            FIELD_DSL_EXPANDED,
            JValueGen::Object(&dsl_obj),
        );
        self
    }
}

impl<'local> JniDataset<'local> {
    pub fn clone_from_dataset(handler: &mut JniHandler<'local>, dataset: &Dataset) -> Self {
        let mut obj = JniDataset::new(handler);
        obj.set_id(&dataset.id);
        obj.set_url(&dataset.url);
        if let Some(url_compact) = &dataset.url_compact {
            obj.set_url_compact(url_compact);
        }
        if let Some(url_medium) = &dataset.url_medium {
            obj.set_url_medium(url_medium);
        }
        if let Some(url_expanded) = &dataset.url_expanded {
            obj.set_url_expanded(url_expanded);
        }
        if let Some(js) = &dataset.js {
            obj.set_js(js);
        }
        if let Some(js_compact) = &dataset.js_compact {
            obj.set_js_compact(js_compact);
        }
        if let Some(js_medium) = &dataset.js_medium {
            obj.set_js_medium(js_medium);
        }
        if let Some(js_expanded) = &dataset.js_expanded {
            obj.set_js_expanded(js_expanded);
        }
        if let Some(dsl) = &dataset.dsl {
            obj.set_dsl(dsl);
        }
        if let Some(dsl_compact) = &dataset.dsl_compact {
            obj.set_dsl_compact(dsl_compact);
        }
        if let Some(dsl_medium) = &dataset.dsl_medium {
            obj.set_dsl_medium(dsl_medium);
        }
        if let Some(dsl_expanded) = &dataset.dsl_expanded {
            obj.set_dsl_expanded(dsl_expanded);
        }
        obj
    }
}
