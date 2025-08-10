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

use jni::{JNIEnv, objects::JObject};

pub(crate) fn throw_java_expception_if_error<T>(
    env: &mut JNIEnv,
    error: Result<T, String>,
) -> Option<T> {
    if let Err(e) = &error {
        env.throw_new("java/lang/Exception", e.as_str()).unwrap();
        return None;
    }
    Some(error.unwrap())
}

pub(crate) fn throw_java_expception_msg(env: &mut JNIEnv, msg: &str) {
    env.throw_new("java/lang/Exception", msg).unwrap();
}

pub struct JavaObject<'a> {
    env: JNIEnv<'a>,
    object: JObject<'a>,
}

impl<'a> JavaObject<'a> {
    pub fn new(env: &JNIEnv<'a>, object: &JObject<'a>) -> Self {
        JavaObject {
            env: unsafe { env.unsafe_clone() },
            object: unsafe { JObject::from_raw(**object) },
        }
    }
    pub fn r#use(&mut self) -> (&mut JNIEnv<'a>, &JObject<'a>) {
        (&mut self.env, &self.object)
    }
}

impl Clone for JavaObject<'_> {
    fn clone(&self) -> Self {
        Self {
            env: unsafe { self.env.unsafe_clone() },
            object: unsafe { JObject::from_raw(*self.object) },
        }
    }
}
