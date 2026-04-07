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

use jni::{
    JValue, jni_sig, jni_str, objects::JObject, refs::Global, signature::MethodSignature,
    strings::JNIStr,
};

use crate::jni::{
    jni_json::JniJson,
    jni_utils::{self, JniFieldDetail},
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
    jni_json: JniJson,
}

impl JniDataset {
    pub fn new() -> Self {
        let jdataset = jni_utils::attach_current_thread(
            |env| -> Result<Global<JObject<'_>>, jni::errors::Error> {
                let jobj = env.new_object(CLASS_NAME, CONSTOR_SIG, &[])?;
                let jobj_ref = env.new_global_ref(jobj)?;
                Ok(jobj_ref)
            },
        )
        .expect("Failed to create JDataset");
        Self {
            dataset: jdataset,
            jni_json: JniJson::new(),
        }
    }

    pub fn jobject_ref(&self) -> &JObject<'_> {
        &self.dataset
    }

    // pub fn unsafe_jobject(&self, env: &Env<'local>) -> JObject<'local> {
    //     unsafe { JObject::from_raw(env, *self.dataset) }
    // }

    pub fn set_id(&mut self, value: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let id_obj = env.new_string(value)?;
                let (name, sig) = FIELD_ID;
                env.set_field(&self.dataset, name, sig, JValue::Object(&id_obj))?;
                env.delete_local_ref(id_obj);
                Ok(())
            })
            .expect("Failed to set id");
        self
    }

    pub fn set_url(&mut self, url: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let url_obj = env.new_string(url)?;
                let (name, sig) = FIELD_URL;
                env.set_field(&self.dataset, name, sig, JValue::Object(&url_obj))?;
                env.delete_local_ref(url_obj);
                Ok(())
            })
            .expect("Failed to set url");
        self
    }

    pub fn set_url_compact(&mut self, url: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let url_obj = env.new_string(url)?;
                let (name, sig) = FIELD_URL_COMPACT;
                env.set_field(&self.dataset, name, sig, JValue::Object(&url_obj))?;
                env.delete_local_ref(url_obj);
                Ok(())
            })
            .expect("Failed to set url compact");
        self
    }

    pub fn set_url_medium(&mut self, url: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let url_obj = env.new_string(url)?;
                let (name, sig) = FIELD_URL_MEDIUM;
                env.set_field(&self.dataset, name, sig, JValue::Object(&url_obj))?;
                env.delete_local_ref(url_obj);
                Ok(())
            })
            .expect("Failed to set url medium");
        self
    }

    pub fn set_url_expanded(&mut self, url: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let url_obj = env.new_string(url)?;
                let (name, sig) = FIELD_URL_EXPANDED;
                env.set_field(&self.dataset, name, sig, JValue::Object(&url_obj))?;
                env.delete_local_ref(url_obj);
                Ok(())
            })
            .expect("Failed to set url expanded");
        self
    }

    pub fn set_js(&mut self, js: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let js_obj = env.new_string(js)?;
                let (name, sig) = FIELD_JS;
                env.set_field(&self.dataset, name, sig, JValue::Object(&js_obj))?;
                env.delete_local_ref(js_obj);
                Ok(())
            })
            .expect("Failed to set js");
        self
    }

    pub fn set_js_compact(&mut self, js: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let js_obj = env.new_string(js)?;
                let (name, sig) = FIELD_JS_COMPACT;
                env.set_field(&self.dataset, name, sig, JValue::Object(&js_obj))?;
                env.delete_local_ref(js_obj);
                Ok(())
            })
            .expect("Failed to set js compact");
        self
    }

    pub fn set_js_medium(&mut self, js: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let js_obj = env.new_string(js)?;
                let (name, sig) = FIELD_JS_MEDIUM;
                env.set_field(&self.dataset, name, sig, JValue::Object(&js_obj))?;
                env.delete_local_ref(js_obj);
                Ok(())
            })
            .expect("Failed to set js medium");
        self
    }

    pub fn set_js_expanded(&mut self, js: &str) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let js_obj = env.new_string(js)?;
                let (name, sig) = FIELD_JS_EXPANDED;
                env.set_field(&self.dataset, name, sig, JValue::Object(&js_obj))?;
                env.delete_local_ref(js_obj);
                Ok(())
            })
            .expect("Failed to set js expanded");
        self
    }

    pub fn set_dsl(&mut self, dsl: &serde_json::Value) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let dsl_obj = self.jni_json.json_value_to_hash_map(env, dsl);
                let (name, sig) = FIELD_DSL;
                env.set_field(&self.dataset, name, sig, JValue::Object(&dsl_obj))?;
                env.delete_local_ref(dsl_obj);
                Ok(())
            })
            .expect("Failed to set dsl");
        self
    }

    pub fn set_dsl_compact(&mut self, dsl: &serde_json::Value) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let dsl_obj = self.jni_json.json_value_to_hash_map(env, dsl);
                let (name, sig) = FIELD_DSL_COMPACT;
                env.set_field(&self.dataset, name, sig, JValue::Object(&dsl_obj))?;
                env.delete_local_ref(dsl_obj);
                Ok(())
            })
            .expect("Failed to set dsl compact");
        self
    }

    pub fn set_dsl_medium(&mut self, dsl: &serde_json::Value) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let dsl_obj = self.jni_json.json_value_to_hash_map(env, dsl);
                let (name, sig) = FIELD_DSL_MEDIUM;
                env.set_field(&self.dataset, name, sig, JValue::Object(&dsl_obj))?;
                env.delete_local_ref(dsl_obj);
                Ok(())
            })
            .expect("Failed to set dsl medium");
        self
    }

    pub fn set_dsl_expanded(&mut self, dsl: &serde_json::Value) -> &mut Self {
        jni_utils::current_java_vm()
            .attach_current_thread(|env| -> Result<(), jni::errors::Error> {
                let dsl_obj = self.jni_json.json_value_to_hash_map(env, dsl);
                let (name, sig) = FIELD_DSL_EXPANDED;
                env.set_field(&self.dataset, name, sig, JValue::Object(&dsl_obj))?;
                env.delete_local_ref(dsl_obj);
                Ok(())
            })
            .expect("Failed to set dsl expanded");
        self
    }
}

impl JniDataset {
    pub fn clone_from_dataset(dataset: &Dataset) -> Self {
        let mut obj = JniDataset::new();
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
