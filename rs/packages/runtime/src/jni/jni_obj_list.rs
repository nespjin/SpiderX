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

use jni::{JNIEnv, errors::Error, objects::JObject};

use crate::jni::jni_constants::JAVA_CLASS_NAME_ARRAY_LIST;

pub fn new_array_list<'local>(env: &'local mut JNIEnv<'local>) -> Result<JObject<'local>, Error> {
    let arr_list_cls = env
        .find_class(JAVA_CLASS_NAME_ARRAY_LIST)
        .expect("Cant find class ArrayList!");

    env.alloc_object(arr_list_cls)
}
