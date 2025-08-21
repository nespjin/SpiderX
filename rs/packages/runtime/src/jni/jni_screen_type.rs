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

use jni::objects::JObject;

use crate::jni::{jni_handler::JniFieldInfo, jni_handler::JniHandler};

pub const CLASS_NAME: &'static str = "com/nesp/spiderx/runtime/model/ScreenType";

pub const FILED_COMPACT: JniFieldInfo = ("COMPACT", "Lcom/nesp/spiderx/runtime/model/ScreenType;");
pub const FILED_MEDIUM: JniFieldInfo = ("MEDIUM", "Lcom/nesp/spiderx/runtime/model/ScreenType;");
pub const FILED_EXPANDED: JniFieldInfo =
    ("EXPANDED", "Lcom/nesp/spiderx/runtime/model/ScreenType;");

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

    pub fn screen_type_to_java_object(&mut self, screen_type: &ScreenType) -> JObject<'local> {
        let enum_value = JniScreenType::get_enum_value_name(screen_type);
        let field_info = if enum_value == FILED_COMPACT.0 {
            FILED_COMPACT
        } else if enum_value == FILED_MEDIUM.0 {
            FILED_MEDIUM
        } else if enum_value == FILED_EXPANDED.0 {
            FILED_EXPANDED
        } else {
            let msg = format!("Invalid screen type: {}", enum_value);
            self.handler.throw_java_expception_msg(&msg);
            FILED_COMPACT
        };
        let ret = self.handler.get_static_field(CLASS_NAME, field_info);
        ret
    }

    pub fn enum_value_to_screen_type(&mut self, enum_value: &str) -> ScreenType {
        if enum_value == FILED_COMPACT.0 {
            ScreenType::Compact
        } else if enum_value == FILED_MEDIUM.0 {
            ScreenType::Medium
        } else if enum_value == FILED_EXPANDED.0 {
            ScreenType::Expanded
        } else {
            let msg = format!("Invalid screen type: {}", enum_value);
            self.handler.throw_java_expception_msg(&msg);
            ScreenType::Compact
        }
    }

    pub fn get_enum_value_name(screen_type: &ScreenType) -> &'static str {
        match screen_type {
            ScreenType::Compact => FILED_COMPACT.0,
            ScreenType::Medium => FILED_MEDIUM.0,
            ScreenType::Expanded => FILED_EXPANDED.0,
        }
    }
}
