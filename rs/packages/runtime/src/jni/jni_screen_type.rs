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
    Env, jni_sig, jni_str,
    objects::{JObject, JString},
    refs::Global,
    strings::JNIStr,
};

use crate::jni::jni_utils::{self, JniFieldDetail};

pub const CLASS_NAME: &'static JNIStr = jni_str!("com/nesp/spiderx/runtime/model/ScreenType");

pub const FILED_COMPACT: JniFieldDetail = (
    jni_str!("COMPACT"),
    jni_sig!(com.nesp.spiderx.runtime.model.ScreenType), // Lcom/nesp/spiderx/runtime/model/ScreenType;
);
pub const FILED_MEDIUM: JniFieldDetail = (
    jni_str!("MEDIUM"),
    jni_sig!(com.nesp.spiderx.runtime.model.ScreenType), // Lcom/nesp/spiderx/runtime/model/ScreenType;
);
pub const FILED_EXPANDED: JniFieldDetail = (
    jni_str!("EXPANDED"),
    jni_sig!(com.nesp.spiderx.runtime.model.ScreenType), // Lcom/nesp/spiderx/runtime/model/ScreenType,
);

pub struct JniScreenType {}

impl JniScreenType {
    pub fn new() -> Self {
        Self {}
    }

    pub fn java_object_to_screen_type(
        &mut self,
        env: &mut Env,
        java_object: JObject,
    ) -> Result<ScreenType, jni::errors::Error> {
        let enum_value = env
            .call_method(
                &java_object,
                jni_str!("name"),
                jni_sig!(() -> java.lang.String),
                &[],
            )?
            .into_object()?;
        let enum_str_value = JString::cast_local(env, enum_value)?.to_string();
        Ok(self.enum_value_to_screen_type(&enum_str_value))
    }

    pub fn screen_type_to_java_object(
        &mut self,
        screen_type: &ScreenType,
    ) -> Global<JObject<'static>> {
        let enum_value = JniScreenType::get_enum_value_name(screen_type);
        let field_info = if enum_value == FILED_COMPACT.0.to_string() {
            FILED_COMPACT
        } else if enum_value == FILED_MEDIUM.0.to_string() {
            FILED_MEDIUM
        } else if enum_value == FILED_EXPANDED.0.to_string() {
            FILED_EXPANDED
        } else {
            let msg = format!("Invalid screen type: {}", enum_value);
            jni_utils::attach_and_throw_java_exception_msg(&msg);
            FILED_COMPACT
        };
        jni_utils::attach_current_thread(
            |env| -> Result<Global<JObject<'static>>, jni::errors::Error> {
                let obj = env
                    .get_static_field(CLASS_NAME, field_info.0, field_info.1)?
                    .into_object()?;
                env.new_global_ref(obj)
            },
        )
        .expect("Failed to new ScreenType")
    }

    pub fn enum_value_to_screen_type(&mut self, enum_value: &str) -> ScreenType {
        if enum_value == FILED_COMPACT.0.to_string() {
            ScreenType::Compact
        } else if enum_value == FILED_MEDIUM.0.to_string() {
            ScreenType::Medium
        } else if enum_value == FILED_EXPANDED.0.to_string() {
            ScreenType::Expanded
        } else {
            let msg = format!("Invalid screen type: {}", enum_value);
            jni_utils::attach_and_throw_java_exception_msg(&msg);
            ScreenType::Compact
        }
    }

    pub fn get_enum_value_name(screen_type: &ScreenType) -> String {
        match screen_type {
            ScreenType::Compact => FILED_COMPACT.0.to_string(),
            ScreenType::Medium => FILED_MEDIUM.0.to_string(),
            ScreenType::Expanded => FILED_EXPANDED.0.to_string(),
        }
    }
}
