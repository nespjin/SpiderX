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

use jni::{
    Env, JValue, jni_sig, jni_str,
    objects::{JObject, JString},
    signature::{FieldSignature, MethodSignature},
    strings::{JNIStr, JNIString},
    vm::JavaVM,
};

pub type JniFieldDetail<'sig> = (&'static JNIStr, FieldSignature<'sig>);
pub type JniMethodDetail<'sig, 'args> = (&'static JNIStr, MethodSignature<'sig, 'args>);

pub const LIST_CLASS: &JNIStr = jni_str!("java/util/List");
pub const LIST_ADD: JniMethodDetail<'_, '_> =
    (jni_str!("add"), jni_sig!((java.lang.Object) -> jboolean));
pub const ARRAY_LIST_CLASS: &JNIStr = jni_str!("java/util/ArrayList");
pub const ARRAY_LIST_CONSTOR: JniMethodDetail<'_, '_> = (ARRAY_LIST_CLASS, jni_sig!(() -> void));

pub const MAP_CLASS: &JNIStr = jni_str!("java/util/Map");
pub const MAP_PUT: JniMethodDetail<'_, '_> = (
    jni_str!("put"),
    jni_sig!((java.lang.Object, java.lang.Object) -> java.lang.Object),
);
pub const HASH_MAP_CLASS: &JNIStr = jni_str!("java/util/HashMap");
pub const HASH_MAP_CONSTOR: JniMethodDetail<'_, '_> = (HASH_MAP_CLASS, jni_sig!(() -> void));

pub const BOOLEAN_CLASS: &JNIStr = jni_str!("java/lang/Boolean");
pub const BOOLEAN_CONSTOR: JniMethodDetail<'_, '_> = (BOOLEAN_CLASS, jni_sig!((jboolean) -> void));

pub const LONG_CLASS: &JNIStr = jni_str!("java/lang/Long");
pub const LONG_CONSTOR: JniMethodDetail<'_, '_> = (LONG_CLASS, jni_sig!((jlong) -> void));

pub const DOUBLE_CLASS: &JNIStr = jni_str!("java/lang/Double");
pub const DOUBLE_CONSTOR: JniMethodDetail<'_, '_> = (DOUBLE_CLASS, jni_sig!((jdouble) -> void));

pub fn current_java_vm() -> JavaVM {
    JavaVM::singleton().expect("Java VM not initialized")
}

pub fn attach_current_thread<F, T, E>(callback: F) -> std::result::Result<T, E>
where
    F: FnOnce(&mut Env) -> std::result::Result<T, E>,
    E: From<jni::errors::Error>,
{
    current_java_vm().attach_current_thread(callback)
}

pub fn set_field(
    env: &mut Env,
    obj: &JObject,
    field: JniFieldDetail,
    value: &JObject,
) -> Result<(), jni::errors::Error> {
    let (name, sig) = field;
    env.set_field(obj, name, sig, JValue::Object(value))
}

pub fn attach_and_throw_java_exception_msg(msg: &str) {
    attach_current_thread(|env| -> Result<(), jni::errors::Error> {
        throw_java_exception_msg(env, msg);
        Ok(())
    })
    .expect("Error in attach_current_thread");
}

pub fn throw_java_exception_msg(env: &mut Env, msg: &str) {
    if let Err(_) = env.throw_new(jni_str!("java/lang/Exception"), JNIString::from(msg)) {
        log::error!("throw_java_exception_msg failed: {}", msg);
    }
}

pub fn throw_java_exception_if_error<T, E>(env: &mut Env, result: Result<T, E>) -> Option<T> {
    if result.is_err() {
        if let Some(msg) = check_jni_exception(env) {
            eprintln!("throw_jni_exception_if_error: {}", msg);
            throw_java_exception_msg(env, &msg);
            return None;
        }
        eprintln!("throw_jni_exception_if_error: Unknown JNI Exception");
        throw_java_exception_msg(env, "Unknown JNI Exception");
        return None;
    }
    result.ok()
}

/// 检查并处理JNI异常
pub fn check_jni_exception(env: &mut Env) -> Option<String> {
    if env.exception_check() {
        // 获取当前异常
        let exception = env.exception_occurred()?;
        if exception.is_null() {
            return None;
        }

        // 打印异常信息到Java控制台
        env.exception_describe();

        // 清除异常状态，避免影响后续操作
        env.exception_clear();

        // 获取异常消息
        let message = match env.call_method(
            exception,
            jni_str!("getMessage"),
            jni_sig!(()-> java.lang.String), // ()Ljava/lang/String;
            &[],
        ) {
            Ok(msg) => {
                let msg_obj: JObject = msg.into_object().ok()?;
                JString::cast_local(env, msg_obj).ok()?.to_string()
            }
            Err(_) => "未知错误".to_string(),
        };

        Some(message)
    } else {
        None
    }
}

pub fn current_thread_name(env: &mut Env) -> String {
    let thread_obj = env
        .call_static_method(
            jni_str!("java/lang/Thread"),
            // ("currentThread", "()Ljava/lang/Thread;"),
            jni_str!("currentThread"),
            jni_sig!(() -> java.lang.Thread),
            &[],
        )
        .expect("Failed to get currentThread")
        .into_object()
        .ok()
        .expect("Failed to get currentThread");
    let thread_name = env
        .call_method(
            &thread_obj,
            jni_str!("getName"),
            jni_sig!(()-> java.lang.String),
            &[],
        )
        .expect("Failed to get thread name")
        .into_object()
        .ok()
        .expect("Failed to get thread name");

    JString::cast_local(env, thread_name)
        .expect("Cant cast object to string")
        .to_string()
}
