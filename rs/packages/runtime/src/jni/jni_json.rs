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
    objects::{JObject, JString, JValueGen},
    sys::{JNI_FALSE, JNI_TRUE},
};

use crate::jni::{jni_classes, jni_hander::JniHandler, jni_methods};

#[derive(Clone)]
pub struct JniJson<'local> {
    handler: JniHandler<'local>,
}

impl<'local> JniJson<'local> {
    pub fn new(handler: JniHandler<'local>) -> Self {
        Self { handler }
    }

    pub fn json_value_to_hash_map(&mut self, value: &serde_json::Value) -> JObject<'local> {
        match value {
            serde_json::Value::Object(map) => {
                let hash_map_obj = self.handler.new_object(jni_classes::HASH_MAP_CONSTOR, &[]);

                for (key, value) in map {
                    let key_obj: JString<'local> = self.handler.new_string(key);
                    let value = self.json_value_to_obj(value);

                    self.handler.call_method(
                        &hash_map_obj,
                        jni_methods::MAP_PUT,
                        &[JValueGen::Object(&key_obj), JValueGen::Object(&value)],
                    );
                }

                hash_map_obj
            }
            _ => JObject::null(),
        }
    }

    pub fn json_value_to_obj(&mut self, value: &serde_json::Value) -> JObject<'local> {
        match value {
            serde_json::Value::Null => JObject::null(),
            serde_json::Value::Bool(value) => self.handler.new_object(
                jni_classes::BOOLEAN_CONSTOR,
                &[JValueGen::Bool(if *value { JNI_TRUE } else { JNI_FALSE })],
            ),
            serde_json::Value::Number(number) => {
                if number.is_i64() || number.is_u64() {
                    self.handler.new_object(
                        jni_classes::LONG_CONSTOR,
                        &[JValueGen::Long(number.as_i64().unwrap())],
                    )
                } else if number.is_f64() {
                    self.handler.new_object(
                        jni_classes::DOUBLE_CONSTOR,
                        &[JValueGen::Double(number.as_f64().unwrap())],
                    )
                } else {
                    let value = &number.to_string();
                    self.handler.new_string(value).into()
                }
            }
            serde_json::Value::String(value) => self.handler.new_string(&value).into(),
            serde_json::Value::Array(values) => {
                let arr_list = self
                    .handler
                    .new_object(jni_classes::ARRAY_LIST_CONSTOR, &[]);

                for value in values {
                    let obj = self.json_value_to_obj(value);
                    self.handler.call_method(
                        &arr_list,
                        jni_methods::LIST_ADD,
                        &[JValueGen::Object(&obj)],
                    );
                }
                arr_list
            }
            serde_json::Value::Object(map) => {
                let hash_map_obj = self.handler.new_object(jni_classes::HASH_MAP_CONSTOR, &[]);
                for (key, value) in map {
                    let key = self.handler.new_string(key);
                    let obj = self.json_value_to_obj(value);
                    self.handler.call_method(
                        &hash_map_obj,
                        jni_methods::MAP_PUT,
                        &[JValueGen::Object(&key), JValueGen::Object(&obj)],
                    );
                }
                hash_map_obj
            }
        }
    }
}
