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

use jni::objects::{JObject, JValueGen};

use crate::jni::{
    jni_classes,
    jni_dataset::JniDataset,
    jni_handler::JniHandler,
    jni_methods,
    jni_screen_type::JniScreenType,
    jni_handler::{JniConstructorInfo, JniFieldInfo},
};

pub const CLASS_NAME: &'static str = "com/nesp/spiderx/runtime/model/Plugin";
pub const PLUGIN_CONSTOR: JniConstructorInfo = (CLASS_NAME, "()V");

pub const FIELD_ID: JniFieldInfo = ("id", "Ljava/lang/String;");
pub const FIELD_NAME: JniFieldInfo = ("name", "Ljava/lang/String;");
pub const FIELD_AUTHOR: JniFieldInfo = ("author", "Ljava/lang/String;");
pub const FIELD_VERSION: JniFieldInfo = ("version", "Ljava/lang/String;");
pub const FIELD_RUNTIME_VERSION: JniFieldInfo = ("runtimeVersion", "Ljava/lang/String;");
pub const FIELD_DESCRIPTION: JniFieldInfo = ("description", "Ljava/lang/String;");
pub const FIELD_TAGS: JniFieldInfo = ("tags", "Ljava/util/List;");
pub const FIELD_SUPPORTED_SCREEN_TYPES: JniFieldInfo = ("supportedScreenTypes", "Ljava/util/List;");
pub const FIELD_DATASETS: JniFieldInfo = ("datasets", "Ljava/util/List;");

pub struct JniPlugin<'local> {
    handler: JniHandler<'local>,
    plugin: JObject<'local>,
}

impl<'local> JniPlugin<'local> {
    pub fn new(handler: &mut JniHandler<'local>) -> Self {
        Self {
            handler: handler.clone(),
            plugin: handler.new_object(PLUGIN_CONSTOR, &[]),
        }
    }

    pub fn jobject_ref(&self) -> &JObject<'local> {
        &self.plugin
    }

    pub fn unsafe_jobject(&self) -> JObject<'local> {
        unsafe { JObject::from_raw(*self.plugin) }
    }

    pub fn set_id(&mut self, id: &str) -> &mut Self {
        let id_obj = self.handler.new_string(id);
        self.handler
            .set_field(&self.plugin, FIELD_ID, JValueGen::Object(&id_obj));
        self
    }

    pub fn set_name(&mut self, name: &str) -> &mut Self {
        let name_obj = self.handler.new_string(name);
        self.handler
            .set_field(&self.plugin, FIELD_NAME, JValueGen::Object(&name_obj));
        self
    }

    pub fn set_author(&mut self, author: &str) -> &mut Self {
        let author_obj = self.handler.new_string(author);
        self.handler
            .set_field(&self.plugin, FIELD_AUTHOR, JValueGen::Object(&author_obj));
        self
    }

    pub fn set_version(&mut self, version: &str) -> &mut Self {
        let version_obj = self.handler.new_string(version);
        self.handler
            .set_field(&self.plugin, FIELD_VERSION, JValueGen::Object(&version_obj));
        self
    }

    pub fn set_runtime_version(&mut self, runtime_version: &str) -> &mut Self {
        let runtime_version_obj = self.handler.new_string(runtime_version);
        self.handler.set_field(
            &self.plugin,
            FIELD_RUNTIME_VERSION,
            JValueGen::Object(&runtime_version_obj),
        );
        self
    }

    pub fn set_description(&mut self, description: &str) -> &mut Self {
        let description_obj = self.handler.new_string(description);
        self.handler.set_field(
            &self.plugin,
            FIELD_DESCRIPTION,
            JValueGen::Object(&description_obj),
        );
        self
    }

    pub fn set_tag_list(&mut self, tag_list: &[String]) -> &mut Self {
        let tags_arr_list = self
            .handler
            .new_object(jni_classes::ARRAY_LIST_CONSTOR, &[]);

        for tag in tag_list {
            let tag_str = self.handler.new_string(tag);
            self.handler.call_method(
                &tags_arr_list,
                jni_methods::LIST_ADD,
                &[JValueGen::Object(&tag_str)],
            );
            self.handler.delete_local_ref(tag_str);
        }
        self.handler
            .set_field(&self.plugin, FIELD_TAGS, JValueGen::Object(&tags_arr_list));
        self
    }

    pub fn set_screen_types(&mut self, screen_types: &[ScreenType]) -> &mut Self {
        let screen_types_arr_list = self
            .handler
            .new_object(jni_classes::ARRAY_LIST_CONSTOR, &[]);
        for screen_type in screen_types {
            let mut jni_screen_type = JniScreenType::new(&mut self.handler);
            let screen_type_obj = jni_screen_type.screen_type_to_java_object(screen_type);
            self.handler.call_method(
                &screen_types_arr_list,
                jni_methods::LIST_ADD,
                &[JValueGen::Object(&screen_type_obj)],
            );
            self.handler.delete_local_ref(screen_type_obj);
        }
        self.handler.set_field(
            &self.plugin,
            FIELD_SUPPORTED_SCREEN_TYPES,
            JValueGen::Object(&screen_types_arr_list),
        );
        self
    }

    pub fn set_datasets(&mut self, datasets: &[Dataset]) -> &mut Self {
        let datasets_arr_list = self
            .handler
            .new_object(jni_classes::ARRAY_LIST_CONSTOR, &[]);
        for dataset in datasets {
            let dataset_obj = JniDataset::clone_from_dataset(&mut self.handler, dataset);
            self.handler.call_method(
                &datasets_arr_list,
                jni_methods::LIST_ADD,
                &[JValueGen::Object(&dataset_obj.jobject_ref())],
            );
            // self.handler.delete_local_ref(dataset_obj);
        }
        self.handler.set_field(
            &self.plugin,
            FIELD_DATASETS,
            JValueGen::Object(&datasets_arr_list),
        );
        self
    }
}

impl<'local> JniPlugin<'local> {
    pub fn clone_from_plugin(handler: &mut JniHandler<'local>, plugin: &Plugin) -> Self {
        let mut obj = JniPlugin::new(handler);
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
