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

use core::data::screen_type::ScreenType;

use jni::{
    JNIEnv,
    objects::{JObject, JString, JValueGen},
};

use crate::jni::{jni_hander::JniHandler, jni_utils};

pub const JAVA_CLASS_NAME_SCREEN_TYPE: &'static str = "com/nesp/spiderx/runtime/model/ScreenType";

pub const JAVA_FILED_SCREEN_TYPE_COMPACT: &'static str = "COMPACT";
pub const JAVA_FILED_SCREEN_TYPE_MEDIUM: &'static str = "MEDIUM";
pub const JAVA_FILED_SCREEN_TYPE_EXPANDED: &'static str = "EXPANDED";

pub struct JniScreenType<'local> {
    handler: JniHandler<'local>,
}

impl<'local> JniScreenType<'local> {
    pub fn new(handler: &mut JniHandler<'local>) -> Self {
        Self {
            handler: handler.clone(),
        }
    }

    pub fn java_object_to_screen_type(&mut self, java_object: JObject) -> ScreenType {
        let enum_value =
            self.handler
                .call_method(&java_object, ("name", "()Ljava/lang/String;"), &[]);
        let enum_value = self.handler.get_string(&enum_value.into());
        self.enum_value_to_screen_type(&enum_value)
    }

    pub fn screen_type_to_java_object(&mut self, screen_type: &ScreenType) -> JObject {
        let enumValue = JniScreenType::get_enum_value_name(screen_type);
        self.get_enum_value(enumValue)
    }

    pub fn enum_value_to_screen_type(&mut self, enumValue: &str) -> ScreenType {
        match enumValue {
            JAVA_FILED_SCREEN_TYPE_COMPACT => ScreenType::Compact,
            JAVA_FILED_SCREEN_TYPE_MEDIUM => ScreenType::Medium,
            JAVA_FILED_SCREEN_TYPE_EXPANDED => ScreenType::Expanded,
            _ => {
                let mut env = jni_utils::JVM
                    .get()
                    .unwrap()
                    .attach_current_thread()
                    .unwrap();
                let msg = format!("Invalid screen type: {}", enumValue);
                jni_utils::throw_java_expception_msg(&mut env, &msg);
                ScreenType::Compact
            }
        }
    }

    pub fn get_enum_value_name(screen_type: &ScreenType) -> &'static str {
        match screen_type {
            ScreenType::Compact => JAVA_FILED_SCREEN_TYPE_COMPACT,
            ScreenType::Medium => JAVA_FILED_SCREEN_TYPE_MEDIUM,
            ScreenType::Expanded => JAVA_FILED_SCREEN_TYPE_EXPANDED,
        }
    }

    pub fn get_enum_value(&mut self, enumValue: &str) -> JObject {
        self.handler.get_static_field(
            JAVA_CLASS_NAME_SCREEN_TYPE,
            (enumValue, &format!("L{};", JAVA_CLASS_NAME_SCREEN_TYPE)),
        );
    }
}
