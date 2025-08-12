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
    strings::JNIString,
    sys::{JNI_FALSE, JNI_TRUE, jboolean},
};

use crate::jni::{
    jni_methods,
    jni_utils::{self, JniConstructorInfo, JniFieldInfo, JniMethodInfo},
};

pub struct JniHandler<'local> {
    env: JNIEnv<'local>,
}
impl<'local> JniHandler<'local> {
    pub fn new() -> Self {
        let env = jni_utils::JVM
            .get()
            .unwrap()
            .attach_current_thread()
            .unwrap();
        Self {
            env: unsafe { env.unsafe_clone() },
        }
    }

    pub fn new_with_env(env: JNIEnv<'local>) -> Self {
        Self { env }
    }

    pub fn new_string<S: Into<JNIString>>(&mut self, from: S) -> JString<'local> {
        let ret = self.env.new_string(from);
        let ret = jni_utils::throw_jni_exception_if_error(&mut self.env, ret).unwrap();
        ret.into()
    }

    pub fn find_class(&mut self, class: &str) -> JClass<'local> {
        let class_ret = self.env.find_class(class);
        self.throw_jni_exception_if_error(class_ret).unwrap()
    }

    pub fn new_object(
        &mut self,
        ctor_info: JniConstructorInfo,
        ctor_args: &[JValue],
    ) -> JObject<'local> {
        let (class_name, sig) = ctor_info;
        let class_ret = self.env.find_class(class_name);
        let class = self.throw_jni_exception_if_error(class_ret);
        class
            .map(|e| self.env.new_object(e, sig, ctor_args))
            .map(|e| self.throw_jni_exception_if_error(e))
            .unwrap()
            .unwrap()
    }

    pub fn call_method(
        &mut self,
        object: &JObject,
        method_info: JniMethodInfo,
        args: &[JValue],
    ) -> JObject<'local> {
        let (mth_name, mth_sig) = method_info;
        let ret = self.env.call_method(object, mth_name, mth_sig, args);
        let jobj_ret = self.throw_jni_exception_if_error(ret).unwrap().l();
        self.throw_jni_exception_if_error(jobj_ret).unwrap()
    }

    pub fn set_field(&mut self, object: &JObject, field_info: JniFieldInfo, value: JValue) {
        let (fld_name, fld_ty) = field_info;
        let ret = self.env.set_field(object, fld_name, fld_ty, value);
        self.throw_jni_exception_if_error(ret).unwrap()
    }

    pub fn get_field(&mut self, object: &JObject, field_info: JniFieldInfo) -> JObject<'local> {
        let (fld_name, fld_ty) = field_info;
        let ret = self.env.get_field(object, fld_name, fld_ty);
        let job_ret = self.throw_jni_exception_if_error(ret).unwrap().l();
        self.throw_jni_exception_if_error(job_ret).unwrap()
    }
    pub fn get_static_field(&mut self, class: &str, field_info: JniFieldInfo) -> JObject<'local> {
        let class_ret = self.env.find_class(class);
        let class: jni::objects::JClass<'local> =
            self.throw_jni_exception_if_error(class_ret).unwrap();
        let (fld_name, fld_ty) = field_info;
        let ret = self.env.get_static_field(class, fld_name, fld_ty);
        let job_ret = self.throw_jni_exception_if_error(ret).unwrap().l();
        self.throw_jni_exception_if_error(job_ret).unwrap()
    }

    pub fn get_string(&mut self, jstring: &JObject<'local>) -> String {
        let ret = self.env.get_string(jstring.into());
        self.throw_jni_exception_if_error(ret).unwrap().into()
    }

    pub fn get_boolean(&mut self, object: &JObject<'local>) -> jboolean {
        let ret = JValueGen::Object(&object).z();
        let ret = self.throw_jni_exception_if_error(ret).unwrap();
        if ret { JNI_TRUE } else { JNI_FALSE }
    }

    pub fn get_class_name(&mut self, object: &JObject<'local>) -> String {
        let class_obj = self.call_method(object, jni_methods::GET_CLASS, &[]);
        let name: JString = self
            .call_method(&class_obj, jni_methods::GET_NAME, &[])
            .into();
        self.get_string(&name)
    }

    pub fn delete_local_ref<'other_local, O>(&mut self, obj: O)
    where
        O: Into<JObject<'other_local>>,
    {
        let ret = self.env.delete_local_ref(obj);
        self.throw_jni_exception_if_error(ret).unwrap();
    }

    pub fn throw_java_expception_if_error<T>(&mut self, error: Result<T, String>) -> Option<T> {
        if let Err(e) = &error {
            println!("throw java expception {}", e.as_str());
            self.env
                .throw_new("java/lang/Exception", e.as_str())
                .unwrap();
            return None;
        }
        Some(error.unwrap())
    }

    pub fn throw_java_expception_msg(&mut self, msg: &str) {
        self.env.throw_new("java/lang/Exception", msg).unwrap();
    }
    pub fn throw_jni_exception_if_error<T>(&mut self, result: errors::Result<T>) -> Option<T> {
        if let Err(_) = result {
            if let Some(msg) = self.check_jni_exception() {
                self.throw_java_expception_msg(&msg);
                return None;
            }
            self.throw_java_expception_msg("Unknown JNI Exception");
            return None;
        }
        return result.ok();
    }

    /// 检查并处理JNI异常
    pub fn check_jni_exception(&mut self) -> Option<String> {
        if self.env.exception_check().unwrap_or(true) {
            // 获取当前异常
            let exception = self.env.exception_occurred().ok()?;
            if exception.is_null() {
                return None;
            }

            // 打印异常信息到Java控制台
            self.env.exception_describe().ok()?;

            // 清除异常状态，避免影响后续操作
            self.env.exception_clear().ok()?;

            // 获取异常消息
            let message =
                match self
                    .env
                    .call_method(exception, "getMessage", "()Ljava/lang/String;", &[])
                {
                    Ok(msg) => {
                        let msg_obj: JObject = msg.l().ok()?;
                        self.env.get_string(&JString::from(msg_obj)).ok()?.into()
                    }
                    Err(_) => "未知错误".to_string(),
                };

            Some(message)
        } else {
            None
        }
    }
}

impl<'local> Clone for JniHandler<'local> {
    fn clone(&self) -> Self {
        Self {
            env: unsafe { self.env.unsafe_clone() },
        }
    }
}
