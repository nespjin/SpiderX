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
    objects::{JObject, JValue},
    sys::jboolean,
};

use crate::jni::{jni_classes, jni_hander::JniHandler, jni_methods};

pub struct JniList<'local> {
    handler: JniHandler<'local>,
    list: JObject<'local>,
}

impl<'local> JniList<'local> {
    pub fn new(handler: &mut JniHandler<'local>) -> Self {
        Self {
            handler: handler.clone(),
            list: handler.new_object(jni_classes::ARRAY_LIST_CONSTOR, &[]),
        }
    }

    pub fn add_obj(&mut self, element: &JObject) -> jboolean {
        let ret: JObject<'_> = self.handler.call_method(
            &self.list,
            jni_methods::LIST_ADD,
            &[JValue::Object(element)],
        );
        self.handler.get_boolean(&ret)
    }
}
