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
    JNIEnv, errors,
    objects::{JClass, JObject, JString, JValue, JValueGen},
    sys::{JNI_FALSE, JNI_TRUE, jboolean},
};
use once_cell::sync::OnceCell;

use crate::jni::jni_methods::{self};

pub static JVM: OnceCell<jni::JavaVM> = OnceCell::new();

pub type JniConstructorInfo = (&'static str, &'static str);
pub type JniMethodInfo = (&'static str, &'static str);
pub type JniFieldInfo = (&'static str, &'static str);

pub fn throw_java_expception_if_error<T>(env: &mut JNIEnv, error: Result<T, String>) -> Option<T> {
    if let Err(e) = &error {
        println!("throw java expception {}", e.as_str());
        env.throw_new("java/lang/Exception", e.as_str()).unwrap();
        return None;
    }
    Some(error.unwrap())
}

pub fn throw_java_expception_msg(env: &mut JNIEnv, msg: &str) {
    env.throw_new("java/lang/Exception", msg).unwrap();
}

pub fn throw_jni_exception_if_error<T>(env: &mut JNIEnv, result: errors::Result<T>) -> Option<T> {
    if let Err(_) = result {
        if let Some(msg) = check_jni_exception() {
            throw_java_expception_msg(env, &msg);
            return None;
        }
        throw_java_expception_msg(env, "Unknown JNI Exception");
        return None;
    }
    return result.ok();
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

pub fn find_class<'local>(env: &mut JNIEnv<'local>, class: &str) -> JClass<'local> {
    let class_ret = env.find_class(class);
    throw_jni_exception_if_error(env, class_ret).unwrap()
}

pub fn new_object<'local>(
    env: &mut JNIEnv<'local>,
    ctor_info: JniConstructorInfo,
    ctor_args: &[JValue],
) -> JObject<'local> {
    // let mut env = JVM.get().unwrap().attach_current_thread().unwrap();
    let (class_name, sig) = ctor_info;
    let class_ret = env.find_class(class_name);
    let class = throw_jni_exception_if_error(env, class_ret);
    class
        .map(|e| env.new_object(e, sig, ctor_args))
        .map(|e| throw_jni_exception_if_error(env, e))
        .unwrap()
        .unwrap()
}

pub fn call_method<'local>(
    env: &mut JNIEnv<'local>,
    object: &JObject<'local>,
    method_info: JniMethodInfo,
    args: &[JValue],
) -> JObject<'local> {
    let (mth_name, mth_sig) = method_info;
    let ret = env.call_method(object, mth_name, mth_sig, args);
    let jobj_ret = throw_jni_exception_if_error(env, ret).unwrap().l();
    throw_jni_exception_if_error(env, jobj_ret).unwrap()
}

pub fn set_field<'local>(
    env: &mut JNIEnv<'local>,
    object: &JObject<'local>,
    field_info: JniFieldInfo,
    value: JValue,
) {
    let (fld_name, fld_ty) = field_info;
    let ret = env.set_field(object, fld_name, fld_ty, value);
    throw_jni_exception_if_error(env, ret).unwrap()
}

pub fn get_field<'local>(
    env: &mut JNIEnv<'local>,
    object: &JObject<'local>,
    field_info: JniFieldInfo,
) -> JObject<'local> {
    let (fld_name, fld_ty) = field_info;
    let ret = env.get_field(object, fld_name, fld_ty);
    let job_ret = throw_jni_exception_if_error(env, ret).unwrap().l();
    throw_jni_exception_if_error(env, job_ret).unwrap()
}
pub fn get_static_field<'local>(
    env: &mut JNIEnv<'local>,
    class: &str,
    field_info: JniFieldInfo,
) -> JObject<'local> {
    let class_ret = env.find_class(class);
    let class: jni::objects::JClass<'local> = throw_jni_exception_if_error(env, class_ret).unwrap();
    let (fld_name, fld_ty) = field_info;
    let ret = env.get_static_field(class, fld_name, fld_ty);
    let job_ret = throw_jni_exception_if_error(env, ret).unwrap().l();
    throw_jni_exception_if_error(env, job_ret).unwrap()
}

pub fn new_string<'local>(env: &'local mut JNIEnv<'local>, string: &'local str) -> JString<'local> {
    let ret = env.new_string(string);
    let ret = throw_jni_exception_if_error(env, ret).unwrap();
    ret.into()
}

pub fn get_string<'local>(env: &mut JNIEnv<'local>, jstring: &JObject<'local>) -> String {
    let ret = env.get_string(jstring.into());
    throw_jni_exception_if_error(env, ret).unwrap().into()
}

pub fn get_boolean<'local>(env: &mut JNIEnv<'local>, object: &JObject<'local>) -> jboolean {
    let ret = JValueGen::Object(&object).z();
    let ret = throw_jni_exception_if_error(env, ret).unwrap();
    if ret { JNI_TRUE } else { JNI_FALSE }
}

pub fn get_class_name<'local>(env: &mut JNIEnv<'local>, object: &JObject<'local>) -> String {
    let class_obj = call_method(env, object, jni_methods::GET_CLASS, &[]);
    let name: JString = call_method(env, &class_obj, jni_methods::GET_NAME, &[]).into();
    get_string(env, &name)
}

pub fn delete_local_ref<'other_local, O>(env: &mut JNIEnv, obj: O)
where
    O: Into<JObject<'other_local>>,
{
    let ret = env.delete_local_ref(obj);
    throw_jni_exception_if_error(env, ret).unwrap();
}
