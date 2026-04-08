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
    Env, jni_sig, jni_str, objects::JObject, refs::Global, signature::MethodSignature,
    strings::JNIStr, sys::jobject,
};

use crate::{
    jni::{
        jni_dataset::JniDataset,
        jni_screen_type::JniScreenType,
        jni_utils::{self, JniFieldDetail},
    },
    jni_attach_and_new_global_ref_obj, jni_set_obj_list_field, jni_set_str_field,
    jni_set_str_list_field,
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
        let jplugin = jni_attach_and_new_global_ref_obj!(CLASS_NAME, CONSTOR_SIG);
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

    pub fn set_id(&mut self, env: &mut Env, value: &str) -> &mut Self {
        jni_set_str_field!(&self.plugin, env, FIELD_ID, value);
        self
    }

    pub fn set_name(&mut self, env: &mut Env, name: &str) -> &mut Self {
        jni_set_str_field!(&self.plugin, env, FIELD_NAME, name);
        self
    }

    pub fn set_author(&mut self, env: &mut Env, author: &str) -> &mut Self {
        jni_set_str_field!(&self.plugin, env, FIELD_AUTHOR, author);
        self
    }

    pub fn set_version(&mut self, env: &mut Env, version: &str) -> &mut Self {
        jni_set_str_field!(&self.plugin, env, FIELD_VERSION, version);
        self
    }

    pub fn set_runtime_version(&mut self, env: &mut Env, runtime_version: &str) -> &mut Self {
        jni_set_str_field!(&self.plugin, env, FIELD_RUNTIME_VERSION, runtime_version);
        self
    }

    pub fn set_description(&mut self, env: &mut Env, description: &str) -> &mut Self {
        jni_set_str_field!(&self.plugin, env, FIELD_DESCRIPTION, description);
        self
    }

    pub fn set_tag_list(&mut self, env: &mut Env, tag_list: &[String]) -> &mut Self {
        jni_set_str_list_field!(&self.plugin, env, FIELD_TAGS, tag_list);
        self
    }

    pub fn set_screen_types(&mut self, env: &mut Env, screen_types: &[ScreenType]) -> &mut Self {
        jni_set_obj_list_field!(
            &self.plugin,
            env,
            FIELD_SUPPORTED_SCREEN_TYPES,
            screen_types,
            |env: &mut Env, screen_type: &ScreenType| {
                let mut jni_screen_type = JniScreenType::new();
                jni_screen_type.screen_type_to_java_object(env, screen_type)
            }
        );
        self
    }

    pub fn set_datasets(&mut self, env: &mut Env, datasets: &[Dataset]) -> &mut Self {
        jni_set_obj_list_field!(
            &self.plugin,
            env,
            FIELD_DATASETS,
            datasets,
            |env: &mut Env, dataset: &Dataset| JniDataset::clone_from_dataset(env, dataset)
                .new_global_ref(env)
        );
        self
    }
}

impl JniPlugin {
    pub fn clone_from_plugin(env: &mut Env, plugin: &Plugin) -> Self {
        let mut obj = JniPlugin::new();
        obj.set_id(env, &plugin.id);
        obj.set_name(env, &plugin.name);
        if let Some(author) = &plugin.author {
            obj.set_author(env, author);
        }
        obj.set_version(env, &plugin.version);
        obj.set_runtime_version(env, &plugin.runtime_version);
        if let Some(description) = &plugin.description {
            obj.set_description(env, description);
        }
        obj.set_tag_list(env, &plugin.tags);
        obj.set_screen_types(env, &plugin.supported_screen_types);
        obj.set_datasets(env, &plugin.datasets);
        obj
    }
}
