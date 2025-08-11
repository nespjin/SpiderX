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

use jni::objects::{JObject, JString, JValueGen};

use crate::jni::jni_utils;

pub const JAVA_CLASS_NAME_SCREEN_TYPE: &'static str = "com/nesp/spiderx/runtime/model/ScreenType";

pub const JAVA_FILED_SCREEN_TYPE_COMPACT: &'static str = "COMPACT";
pub const JAVA_FILED_SCREEN_TYPE_MEDIUM: &'static str = "MEDIUM";
pub const JAVA_FILED_SCREEN_TYPE_EXPANDED: &'static str = "EXPANDED";

pub fn java_object_to_screen_type(java_object: JObject) -> ScreenType {
    let mut env = jni_utils::JVM
        .get()
        .unwrap()
        .attach_current_thread()
        .unwrap();
    let enum_value = env
        .call_method(&java_object, "name", "()Ljava/lang/String;", &[])
        .expect("Unable to get ScreenType enum value!");
    let enum_value: String = match enum_value {
        JValueGen::Object(obj) => {
            let jstr: JString = obj.into();
            let str: String = env.get_string(&jstr).unwrap().into();
            str
        }
        _ => {
            let mut env = jni_utils::JVM
                .get()
                .unwrap()
                .attach_current_thread()
                .unwrap();
            let msg = format!("Invalid screen type: {:?}", enum_value);
            jni_utils::throw_java_expception_msg(&mut env, &msg);
            "".to_string()
        }
    };
    enum_value_to_screen_type(&enum_value)
}

pub fn screen_type_to_java_object(screen_type: &ScreenType) -> JObject {
    let enumValue = get_enum_value_name(screen_type);
    get_enum_value(enumValue)
}

pub fn enum_value_to_screen_type(enumValue: &str) -> ScreenType {
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

pub fn get_enum_value(enumValue: &str) -> JObject {
    let mut env = jni_utils::JVM
        .get()
        .unwrap()
        .attach_current_thread()
        .unwrap();
    let class = env
        .find_class(JAVA_CLASS_NAME_SCREEN_TYPE)
        .expect("ScreenType class not found!");
    env.get_static_field(
        class,
        enumValue,
        &format!("L{};", JAVA_CLASS_NAME_SCREEN_TYPE),
    )
    .expect(&format!(
        "Unable to get ScreenType enum value {}!",
        enumValue
    ))
    .l()
    .expect("Failed to convert ScreenType enum value to jobject!")
}
