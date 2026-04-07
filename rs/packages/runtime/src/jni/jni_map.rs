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

// use jni::objects::{JObject, JValue};

// use crate::jni::{jni_classes, jni_handler::JniHandler, jni_methods};

// pub struct JniMap<'local> {
//     handler: JniHandler<'local>,
//     map: JObject<'local>,
// }

// impl<'local> JniMap<'local> {
//     pub fn new(handler: &mut JniHandler<'local>) -> Self {
//         Self {
//             handler: handler.clone(),
//             map: handler.new_object(jni_classes::HASH_MAP_CONSTOR, &[]),
//         }
//     }

//     pub fn put(&mut self, key: &JObject, value: &JObject) {
//         self.handler.call_method(
//             &self.map,
//             jni_methods::MAP_PUT,
//             &[JValue::Object(&key), JValue::Object(&value)],
//         );
//     }
// }
