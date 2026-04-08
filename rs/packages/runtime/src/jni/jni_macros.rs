// Copyright (c) 2026. NESP Technology Corporation.
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

#[macro_export]
macro_rules! jni_set_str_field {
    ($self:expr, $env:expr, $field:ident, $value:expr) => {
        (|env: &mut Env| -> Result<(), jni::errors::Error> {
            let field_detail: jni_utils::JniFieldDetail = $field;
            let value_str: &str = $value;
            let value_obj = env.new_string(value_str)?;
            let (name, sig) = field_detail;
            env.set_field($self, name, sig, jni::objects::JValue::Object(&value_obj))?;
            env.delete_local_ref(value_obj);
            Ok(())
        })($env)
        .expect(&format!("Failed to set field {:?}", $field));
    };
}

#[macro_export]
macro_rules! jni_set_obj_list_field {
    ($self:expr, $env:expr, $field:ident, $items:expr, $item_to_java:expr) => {
        (|env: &mut Env| -> Result<(), jni::errors::Error> {
            let field_detail: jni_utils::JniFieldDetail = $field;
            let arr_list = env.new_object(
                jni_utils::ARRAY_LIST_CONSTOR.0,
                jni_utils::ARRAY_LIST_CONSTOR.1,
                &[],
            )?;

            for item in $items {
                let item_obj_result: Result<
                    jni::refs::Global<jni::objects::JObject<'static>>,
                    jni::errors::Error,
                > = $item_to_java(env, item);
                let item_obj = item_obj_result?;
                env.call_method(
                    &arr_list,
                    jni_utils::LIST_ADD.0,
                    jni_utils::LIST_ADD.1,
                    &[jni::objects::JValue::Object(&item_obj)],
                )?;
            }
            env.set_field(
                $self,
                field_detail.0,
                field_detail.1,
                jni::objects::JValue::Object(&arr_list),
            )?;
            env.delete_local_ref(arr_list);
            Ok(())
        })($env)
        .expect(&format!("Failed to set field {:?}", $field));
    };
}

#[macro_export]
macro_rules! jni_set_str_list_field {
    ($self:expr, $env:expr, $field:ident, $items:expr) => {
        (|env: &mut Env| -> Result<(), jni::errors::Error> {
            let field_detail: jni_utils::JniFieldDetail = $field;
            let arr_list = env.new_object(
                jni_utils::ARRAY_LIST_CONSTOR.0,
                jni_utils::ARRAY_LIST_CONSTOR.1,
                &[],
            )?;

            for item in $items {
                let item_str = env.new_string(item)?;
                env.call_method(
                    &arr_list,
                    jni_utils::LIST_ADD.0,
                    jni_utils::LIST_ADD.1,
                    &[jni::objects::JValue::Object(&item_str)],
                )?;
                env.delete_local_ref(item_str);
            }
            env.set_field(
                $self,
                field_detail.0,
                field_detail.1,
                jni::objects::JValue::Object(&arr_list),
            )?;
            env.delete_local_ref(arr_list);
            Ok(())
        })($env)
        .expect(&format!("Failed to set field {:?}", $field));
    };
}

#[macro_export]
macro_rules! jni_set_json_value_field {
    ($self:expr, $env:expr, $field:ident, $value:expr) => {
        (|env: &mut Env| -> Result<(), jni::errors::Error> {
            let field_detail: jni_utils::JniFieldDetail = $field;
            let json_value: &serde_json::Value = $value;
            let json_obj = jni_utils::json_value_to_hash_map(env, json_value);
            let (name, sig) = field_detail;
            env.set_field($self, name, sig, jni::objects::JValue::Object(&json_obj))?;
            env.delete_local_ref(json_obj);
            Ok(())
        })($env)
        .expect(&format!("Failed to set field {:?}", $field));
    };
}
