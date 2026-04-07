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
    plugin::{Dataset, Plugin},
    screen_type::ScreenType,
};
use jni::{
    jni_sig, jni_str,
    objects::{JObject, JValue},
    refs::Global,
    signature::MethodSignature,
    strings::JNIStr,
    sys::jobject,
};

use crate::jni::{
    jni_dataset::JniDataset,
    jni_screen_type::JniScreenType,
    jni_utils::{self, JniFieldDetail},
};

pub const CLASS_NAME: &'static JNIStr = jni_str!("com/nesp/spiderx/runtime/model/Plugin");
pub const CONSTOR_SIG: MethodSignature = jni_sig!(()->void);

pub const FIELD_ID: JniFieldDetail = (jni_str!("id"), jni_sig!(java.lang.String));
pub const FIELD_NAME: JniFieldDetail = (jni_str!("name"), jni_sig!(java.lang.String));
pub const FIELD_AUTHOR: JniFieldDetail = (jni_str!("author"), jni_sig!(java.lang.String));
pub const FIELD_VERSION: JniFieldDetail = (jni_str!("version"), jni_sig!(java.lang.String));
pub const FIELD_RUNTIME_VERSION: JniFieldDetail =
    (jni_str!("runtimeVersion"), jni_sig!(java.lang.String));
pub const FIELD_DESCRIPTION: JniFieldDetail = (jni_str!("description"), jni_sig!(java.lang.String));
pub const FIELD_TAGS: JniFieldDetail = (jni_str!("tags"), jni_sig!(java.util.List));
pub const FIELD_SUPPORTED_SCREEN_TYPES: JniFieldDetail =
    (jni_str!("supportedScreenTypes"), jni_sig!(java.util.List));
pub const FIELD_DATASETS: JniFieldDetail = (jni_str!("datasets"), jni_sig!(java.util.List));

pub struct JniPlugin {
    plugin: Global<JObject<'static>>,
}

impl JniPlugin {
    pub fn new() -> Self {
        let jplugin = jni_utils::attach_current_thread(
            |env| -> Result<Global<JObject<'_>>, jni::errors::Error> {
                let jobj = env.new_object(CLASS_NAME, CONSTOR_SIG, &[])?;
                let jobj_ref = env.new_global_ref(jobj)?;
                Ok(jobj_ref)
            },
        )
        .expect("Failed to create JPlugin");
        Self { plugin: jplugin }
    }

    pub fn jobject_ref(&self) -> &JObject<'_> {
        &self.plugin
    }

    pub fn into_raw_jobject(&self) -> jobject {
        self.plugin.as_raw()
    }

    // pub fn unsafe_jobject(&self,env:&mut Env) -> JObject<'local> {
    //     self.handler.object_from_raw(*self.plugin)
    // }

    pub fn set_id(&mut self, value: &str) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let id_obj = env.new_string(value)?;
            let (name, sig) = FIELD_ID;
            env.set_field(&self.plugin, name, sig, JValue::Object(&id_obj))?;
            env.delete_local_ref(id_obj);
            Ok(())
        })
        .expect("Failed to set id");
        self
    }

    pub fn set_name(&mut self, name: &str) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let name_obj = env.new_string(name)?;
            let (name, sig) = FIELD_NAME;
            env.set_field(&self.plugin, name, sig, JValue::Object(&name_obj))?;
            env.delete_local_ref(name_obj);
            Ok(())
        })
        .expect("Failed to set name");
        self
    }

    pub fn set_author(&mut self, author: &str) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let author_obj = env.new_string(author)?;
            let (name, sig) = FIELD_AUTHOR;
            env.set_field(&self.plugin, name, sig, JValue::Object(&author_obj))?;
            env.delete_local_ref(author_obj);
            Ok(())
        })
        .expect("Failed to set author");
        self
    }

    pub fn set_version(&mut self, version: &str) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let version_obj = env.new_string(version)?;
            let (name, sig) = FIELD_VERSION;
            env.set_field(&self.plugin, name, sig, JValue::Object(&version_obj))?;
            env.delete_local_ref(version_obj);
            Ok(())
        })
        .expect("Failed to set version");
        self
    }

    pub fn set_runtime_version(&mut self, runtime_version: &str) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let version_obj = env.new_string(runtime_version)?;
            let (name, sig) = FIELD_RUNTIME_VERSION;
            env.set_field(&self.plugin, name, sig, JValue::Object(&version_obj))?;
            env.delete_local_ref(version_obj);
            Ok(())
        })
        .expect("Failed to set runtime version");
        self
    }

    pub fn set_description(&mut self, description: &str) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let description_obj = env.new_string(description)?;
            let (name, sig) = FIELD_DESCRIPTION;
            env.set_field(&self.plugin, name, sig, JValue::Object(&description_obj))?;
            env.delete_local_ref(description_obj);
            Ok(())
        })
        .expect("Failed to set description");
        self
    }

    pub fn set_tag_list(&mut self, tag_list: &[String]) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let tags_arr_list = env.new_object(
                jni_utils::ARRAY_LIST_CONSTOR.0,
                jni_utils::ARRAY_LIST_CONSTOR.1,
                &[],
            )?;

            for tag in tag_list {
                let tag_str = env.new_string(tag)?;
                env.call_method(
                    &tags_arr_list,
                    jni_utils::LIST_ADD.0,
                    jni_utils::LIST_ADD.1,
                    &[JValue::Object(&tag_str)],
                )?;
                env.delete_local_ref(tag_str);
            }
            env.set_field(
                &self.plugin,
                FIELD_TAGS.0,
                FIELD_TAGS.1,
                JValue::Object(&tags_arr_list),
            )?;
            env.delete_local_ref(tags_arr_list);
            Ok(())
        })
        .expect("Failed to set tag list");
        self
    }

    pub fn set_screen_types(&mut self, screen_types: &[ScreenType]) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let screen_types_arr_list = env.new_object(
                jni_utils::ARRAY_LIST_CONSTOR.0,
                jni_utils::ARRAY_LIST_CONSTOR.1,
                &[],
            )?;
            for screen_type in screen_types {
                let mut jni_screen_type = JniScreenType::new();
                let screen_type_obj = jni_screen_type.screen_type_to_java_object(screen_type);
                env.call_method(
                    &screen_types_arr_list,
                    jni_utils::LIST_ADD.0,
                    jni_utils::LIST_ADD.1,
                    &[JValue::Object(&screen_type_obj)],
                )?;
                // env.delete_local_ref(screen_type_obj);
            }
            env.set_field(
                &self.plugin,
                FIELD_SUPPORTED_SCREEN_TYPES.0,
                FIELD_SUPPORTED_SCREEN_TYPES.1,
                JValue::Object(&screen_types_arr_list),
            )?;
            env.delete_local_ref(screen_types_arr_list);

            Ok(())
        }).expect("Error in set_screen_type");
        self
    }

    pub fn set_datasets(&mut self, datasets: &[Dataset]) -> &mut Self {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let datasets_arr_list = env.new_object(
                jni_utils::ARRAY_LIST_CONSTOR.0,
                jni_utils::ARRAY_LIST_CONSTOR.1,
                &[],
            )?;
            for dataset in datasets {
                let dataset_obj = JniDataset::clone_from_dataset(dataset);
                env.call_method(
                    &datasets_arr_list,
                    jni_utils::LIST_ADD.0,
                    jni_utils::LIST_ADD.1,
                    &[JValue::Object(&dataset_obj.jobject_ref())],
                )?;
                // self.handler.delete_local_ref(dataset_obj);
            }
            env.set_field(
                &self.plugin,
                FIELD_DATASETS.0,
                FIELD_DATASETS.1,
                JValue::Object(&datasets_arr_list),
            )?;
            env.delete_local_ref(datasets_arr_list);
            Ok(())
        })
        .expect("Failed to set datasets");
        self
    }
}

impl JniPlugin {
    pub fn clone_from_plugin(plugin: &Plugin) -> Self {
        let mut obj = JniPlugin::new();
        obj.set_id(&plugin.id);
        obj.set_name(&plugin.name);
        if let Some(author) = &plugin.author {
            obj.set_author(author);
        }
        obj.set_version(&plugin.version);
        obj.set_runtime_version(&plugin.runtime_version);
        if let Some(description) = &plugin.description {
            obj.set_description(description);
        }
        obj.set_tag_list(&plugin.tags);
        obj.set_screen_types(&plugin.supported_screen_types);
        obj.set_datasets(&plugin.datasets);
        obj
    }
}
