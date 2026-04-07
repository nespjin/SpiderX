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

use jni::{
    Env,
    objects::{JObject, JString, JValue},
};

use crate::jni::jni_utils;

#[derive(Clone)]
pub struct JniJson {}

impl JniJson {
    pub fn new() -> Self {
        Self {}
    }

    pub fn json_value_to_hash_map<'local>(
        &self,
        env: &mut Env<'local>,
        value: &serde_json::Value,
    ) -> JObject<'local> {
        match value {
            serde_json::Value::Object(map) => {
                let (name, sig) = jni_utils::HASH_MAP_CONSTOR;
                let hash_map_obj = env
                    .new_object(name, sig, &[])
                    .expect("Failed to new HashMap");

                for (key, value) in map {
                    let key_obj: JString<'local> =
                        env.new_string(key).expect("Faild to new String");
                    let value = self.json_value_to_obj(env, value);

                    let (name, sig) = jni_utils::MAP_PUT;
                    env.call_method(
                        &hash_map_obj,
                        name,
                        sig,
                        &[JValue::Object(&key_obj), JValue::Object(&value)],
                    )
                    .expect("Failed to call Map.put");
                    env.delete_local_ref(key_obj);
                    env.delete_local_ref(value);
                }

                hash_map_obj
            }
            _ => JObject::null(),
        }
    }

    pub fn json_value_to_obj<'local>(
        &self,
        env: &mut Env<'local>,
        value: &serde_json::Value,
    ) -> JObject<'local> {
        match value {
            serde_json::Value::Null => JObject::null(),
            serde_json::Value::Bool(value) => env
                .new_object(
                    jni_utils::BOOLEAN_CONSTOR.0,
                    jni_utils::BOOLEAN_CONSTOR.1,
                    &[JValue::Bool(*value)],
                )
                .expect("Failed to new Boolean"),
            serde_json::Value::Number(number) => {
                if number.is_i64() || number.is_u64() {
                    env.new_object(
                        jni_utils::LONG_CONSTOR.0,
                        jni_utils::LONG_CONSTOR.1,
                        &[JValue::Long(number.as_i64().unwrap())],
                    )
                    .expect("Failed to new Long")
                } else if number.is_f64() {
                    env.new_object(
                        jni_utils::DOUBLE_CONSTOR.0,
                        jni_utils::DOUBLE_CONSTOR.1,
                        &[JValue::Double(number.as_f64().unwrap())],
                    )
                    .expect("Failed to new Double")
                } else {
                    let value = &number.to_string();
                    env.new_string(value).expect("Failed to new String").into()
                }
            }
            serde_json::Value::String(value) => {
                env.new_string(&value).expect("Failed to new String").into()
            }
            serde_json::Value::Array(values) => {
                let arr_list = env
                    .new_object(
                        jni_utils::ARRAY_LIST_CONSTOR.0,
                        jni_utils::ARRAY_LIST_CONSTOR.1,
                        &[],
                    )
                    .expect("Failed to new ArrayList");

                for value in values {
                    let obj = self.json_value_to_obj(env, value);

                    env.call_method(
                        &arr_list,
                        jni_utils::LIST_ADD.0,
                        jni_utils::LIST_ADD.1,
                        &[JValue::Object(&obj)],
                    )
                    .expect("Failed to call ArrayList.add");
                }
                arr_list
            }
            serde_json::Value::Object(map) => {
                let hash_map_obj = env
                    .new_object(
                        jni_utils::HASH_MAP_CONSTOR.0,
                        jni_utils::HASH_MAP_CONSTOR.1,
                        &[],
                    )
                    .expect("Failed to new HashMap");
                for (key, value) in map {
                    let key = env.new_string(key).expect("Failed to new String");
                    let obj = self.json_value_to_obj(env, value);
                    env.call_method(
                        &hash_map_obj,
                        jni_utils::MAP_PUT.0,
                        jni_utils::MAP_PUT.1,
                        &[JValue::Object(&key), JValue::Object(&obj)],
                    )
                    .expect("Failed to call Map.put");
                    env.delete_local_ref(key);
                    env.delete_local_ref(obj);
                }
                hash_map_obj
            }
        }
    }
}
