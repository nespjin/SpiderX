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

use spiderx_core::data::plugin::Dataset;

use jni::{
    Env, jni_sig, jni_str, objects::JObject, refs::Global, signature::MethodSignature,
    strings::JNIStr,
};

use crate::{
    jni::jni_utils::{self, JniFieldDetail},
    jni_attach_and_new_global_ref_obj, jni_set_json_value_field, jni_set_str_field,
};

pub const CLASS_NAME: &'static JNIStr = jni_str!("com/nesp/spiderx/runtime/model/Dataset");
pub const CONSTOR_SIG: MethodSignature = jni_sig!(()->void);

pub const FIELD_ID: JniFieldDetail = (
    jni_str!("id"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_URL: JniFieldDetail = (
    jni_str!("url"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);

pub const FIELD_URL_COMPACT: JniFieldDetail = (
    jni_str!("urlCompact"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_URL_MEDIUM: JniFieldDetail = (
    jni_str!("urlMedium"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_URL_EXPANDED: JniFieldDetail = (
    jni_str!("urlExpanded"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_JS: JniFieldDetail = (
    jni_str!("js"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_JS_COMPACT: JniFieldDetail = (
    jni_str!("jsCompact"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_JS_MEDIUM: JniFieldDetail = (
    jni_str!("jsMedium"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_JS_EXPANDED: JniFieldDetail = (
    jni_str!("jsExpanded"),
    jni_sig!(java.lang.String), // Ljava/lang/String;
);
pub const FIELD_DSL: JniFieldDetail = (
    jni_str!("dsl"),
    jni_sig!(java.util.Map), // Ljava/util/Map;
);
pub const FIELD_DSL_COMPACT: JniFieldDetail = (
    jni_str!("dslCompact"),
    jni_sig!(java.util.Map), // Ljava/util/Map;
);
pub const FIELD_DSL_MEDIUM: JniFieldDetail = (
    jni_str!("dslMedium"),
    jni_sig!(java.util.Map), // Ljava/util/Map;
);
pub const FIELD_DSL_EXPANDED: JniFieldDetail = (
    jni_str!("dslExpanded"),
    jni_sig!(java.util.Map), // Ljava/util/Map;
);

pub struct JniDataset {
    dataset: Global<JObject<'static>>,
}

impl JniDataset {
    pub fn new() -> Self {
        let jdataset = jni_attach_and_new_global_ref_obj!(CLASS_NAME, CONSTOR_SIG);
        Self { dataset: jdataset }
    }

    pub fn new_global_ref(
        &self,
        env: &Env<'_>,
    ) -> Result<Global<JObject<'static>>, jni::errors::Error> {
        env.new_global_ref(&self.dataset)
    }

    // pub fn unsafe_jobject(&self, env: &Env<'local>) -> JObject<'local> {
    //     unsafe { JObject::from_raw(env, *self.dataset) }
    // }

    pub fn set_id(&mut self, env: &mut Env, value: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_ID, value);
        self
    }

    pub fn set_url(&mut self, env: &mut Env, url: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_URL, url);
        self
    }

    pub fn set_url_compact(&mut self, env: &mut Env, url: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_URL_COMPACT, url);
        self
    }

    pub fn set_url_medium(&mut self, env: &mut Env, url: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_URL_MEDIUM, url);
        self
    }

    pub fn set_url_expanded(&mut self, env: &mut Env, url: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_URL_EXPANDED, url);
        self
    }

    pub fn set_js(&mut self, env: &mut Env, js: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_JS, js);
        self
    }

    pub fn set_js_compact(&mut self, env: &mut Env, js: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_JS_COMPACT, js);
        self
    }

    pub fn set_js_medium(&mut self, env: &mut Env, js: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_JS_MEDIUM, js);
        self
    }

    pub fn set_js_expanded(&mut self, env: &mut Env, js: &str) -> &mut Self {
        jni_set_str_field!(&self.dataset, env, FIELD_JS_EXPANDED, js);
        self
    }

    pub fn set_dsl(&mut self, env: &mut Env, dsl: &serde_json::Value) -> &mut Self {
        jni_set_json_value_field!(&self.dataset, env, FIELD_DSL, dsl);
        self
    }

    pub fn set_dsl_compact(&mut self, env: &mut Env, dsl: &serde_json::Value) -> &mut Self {
        jni_set_json_value_field!(&self.dataset, env, FIELD_DSL_COMPACT, dsl);
        self
    }

    pub fn set_dsl_medium(&mut self, env: &mut Env, dsl: &serde_json::Value) -> &mut Self {
        jni_set_json_value_field!(&self.dataset, env, FIELD_DSL_MEDIUM, dsl);
        self
    }

    pub fn set_dsl_expanded(&mut self, env: &mut Env, dsl: &serde_json::Value) -> &mut Self {
        jni_set_json_value_field!(&self.dataset, env, FIELD_DSL_EXPANDED, dsl);
        self
    }
}

impl JniDataset {
    pub fn clone_from_dataset(env: &mut Env, dataset: &Dataset) -> Self {
        let mut obj = JniDataset::new();
        obj.set_id(env, &dataset.id);
        obj.set_url(env, &dataset.url);
        if let Some(url_compact) = &dataset.url_compact {
            obj.set_url_compact(env, url_compact);
        }
        if let Some(url_medium) = &dataset.url_medium {
            obj.set_url_medium(env, url_medium);
        }
        if let Some(url_expanded) = &dataset.url_expanded {
            obj.set_url_expanded(env, url_expanded);
        }
        if let Some(js) = &dataset.js {
            obj.set_js(env, js);
        }
        if let Some(js_compact) = &dataset.js_compact {
            obj.set_js_compact(env, js_compact);
        }
        if let Some(js_medium) = &dataset.js_medium {
            obj.set_js_medium(env, js_medium);
        }
        if let Some(js_expanded) = &dataset.js_expanded {
            obj.set_js_expanded(env, js_expanded);
        }
        if let Some(dsl) = &dataset.dsl {
            obj.set_dsl(env, dsl);
        }
        if let Some(dsl_compact) = &dataset.dsl_compact {
            obj.set_dsl_compact(env, dsl_compact);
        }
        if let Some(dsl_medium) = &dataset.dsl_medium {
            obj.set_dsl_medium(env, dsl_medium);
        }
        if let Some(dsl_expanded) = &dataset.dsl_expanded {
            obj.set_dsl_expanded(env, dsl_expanded);
        }
        obj
    }
}
