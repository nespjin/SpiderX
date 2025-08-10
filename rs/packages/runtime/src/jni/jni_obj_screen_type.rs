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

use jni::{objects::JValueOwned, JNIEnv};

pub const JAVA_CLASS_NAME_SCREEN_TYPE: &'static str = "com/nesp/spiderx/runtime/data/ScreenType";

pub const JAVA_FILED_SCREEN_TYPE_COMPACT: &'static str = "COMPACT";
pub const JAVA_FILED_SCREEN_TYPE_MEDIUM: &'static str = "MEDIUM";
pub const JAVA_FILED_SCREEN_TYPE_EXPANDED: &'static str = "EXPANDED";

pub fn screen_type_to_java_object<'local>(
    env: &'local mut JNIEnv,
    screen_type: &'local ScreenType,
) -> JValueOwned<'local> {
    let enumValue = get_enum_value_name(screen_type);
    get_enum_value(env, enumValue)
}

pub fn get_enum_value_name(screen_type: &ScreenType) -> &'static str {
    match screen_type {
        ScreenType::Compact => JAVA_FILED_SCREEN_TYPE_COMPACT,
        ScreenType::Medium => JAVA_FILED_SCREEN_TYPE_MEDIUM,
        ScreenType::Expanded => JAVA_FILED_SCREEN_TYPE_EXPANDED,
    }
}

pub fn get_enum_value<'local>(env: &'local mut JNIEnv, enumValue: &'local str) -> JValueOwned<'local> {
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
    // .l()
    // .expect("Failed to convert ScreenType enum value to jobject!")
}
