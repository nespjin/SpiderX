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
    JNIEnv,
    objects::{JObject, JString},
};
use once_cell::sync::OnceCell;

pub static JVM: OnceCell<jni::JavaVM> = OnceCell::new();

pub fn throw_java_expception_if_error<T>(env: &mut JNIEnv, error: Result<T, String>) -> Option<T> {
    if let Err(e) = &error {
        env.throw_new("java/lang/Exception", e.as_str()).unwrap();
        return None;
    }
    Some(error.unwrap())
}

pub fn throw_java_expception_msg(env: &mut JNIEnv, msg: &str) {
    env.throw_new("java/lang/Exception", msg).unwrap();
}

/// 检查并处理JNI异常
pub fn check_jni_exception() -> Option<String> {
    let mut env = JVM.get().unwrap().attach_current_thread().unwrap();
    if env.exception_check().unwrap_or(true) {
        // 获取当前异常
        let exception = env.exception_occurred().ok()?;
        if exception.is_null() {
            return None;
        }

        // 打印异常信息到Java控制台
        env.exception_describe().ok()?;

        // 清除异常状态，避免影响后续操作
        env.exception_clear().ok()?;

        // 获取异常消息
        let message = match env.call_method(exception, "getMessage", "()Ljava/lang/String;", &[]) {
            Ok(msg) => {
                let msg_obj: JObject = msg.l().ok()?;
                env.get_string(&JString::from(msg_obj)).ok()?.into()
            }
            Err(_) => "未知错误".to_string(),
        };

        Some(message)
    } else {
        None
    }
}

pub struct JObjectOwned<'a> {
    env: JNIEnv<'a>,
    object: JObject<'a>,
}

impl<'a> JObjectOwned<'a> {
    pub fn new(env: &JNIEnv<'a>, object: &JObject<'a>) -> Self {
        JObjectOwned {
            env: unsafe { env.unsafe_clone() },
            object: unsafe { JObject::from_raw(**object) },
        }
    }
    pub fn r#use(&mut self) -> (&mut JNIEnv<'a>, &JObject<'a>) {
        (&mut self.env, &self.object)
    }
}

impl Clone for JObjectOwned<'_> {
    fn clone(&self) -> Self {
        Self {
            env: unsafe { self.env.unsafe_clone() },
            object: unsafe { JObject::from_raw(*self.object) },
        }
    }
}
