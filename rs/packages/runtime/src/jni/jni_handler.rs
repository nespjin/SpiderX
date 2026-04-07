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

// use std::fmt::Display;

// use jni::{
//     Env, JValueOwned, JavaVM,
//     descriptors::Desc,
//     errors, jni_sig, jni_str,
//     objects::{AsJArrayRaw, Global, JByteArray, JClass, JObject, JString, JValue},
//     refs::Reference,
//     signature::{FieldSignature, MethodSignature},
//     strings::{JNIStr, JNIString},
//     sys::{JNI_FALSE, JNI_TRUE, jboolean, jbyte, jobject, jsize},
// };

// use crate::jni::jni_methods;

// pub type JniConstructorInfo = (&'static str, &'static str);
// pub type JniMethodInfo = (&'static str, &'static str);
// pub type JniFieldInfo = (&'static str, &'static str);

// pub struct JniHandler {}

// impl JniHandler {
//     pub fn new() -> Self {
//         Self {}
//     }

//     pub fn new_string<'local, S: AsRef<str>>(&self, env: &mut Env, from: S) -> JString<'local> {
//         let ret = env.new_string(from);
//         let ret = self
//             .throw_jni_exception_if_error(ret)
//             .unwrap_or(JString::null());
//         ret.into()
//     }

//     pub fn find_class(&mut self, class: &str) -> JClass<'local> {
//         let class_ret = self.env.find_class(jni_str!("class"));
//         self.throw_jni_exception_if_error(class_ret).unwrap()
//     }

//     pub fn new_object_with_class<'other_local, T, U>(
//         &mut self,
//         class: T,
//         ctor_sig: U,
//         ctor_args: &[JValue],
//     ) -> JObject<'local>
//     where
//         T: Desc<'local, JClass<'other_local>>,
//         U: Into<JNIString> + AsRef<str>,
//     {
//         let obj_ret = self.env.new_object(class, ctor_sig, ctor_args);
//         self.throw_jni_exception_if_error(obj_ret).unwrap()
//     }

//     pub fn new_object(
//         &mut self,
//         ctor_info: JniConstructorInfo,
//         ctor_args: &[JValue],
//     ) -> JObject<'local> {
//         let (class_name, sig) = ctor_info;
//         let class_ret = self.env.find_class(class_name);
//         let class = self.throw_jni_exception_if_error(class_ret);
//         class
//             .map(|e| self.env.new_object(e, sig, ctor_args))
//             .map(|e| self.throw_jni_exception_if_error(e))
//             .unwrap()
//             .unwrap()
//     }

//     pub fn object_from_raw(&mut self, raw: jobject) -> JObject<'local> {
//         unsafe { JObject::from_raw(self.env, raw) }
//     }

//     pub fn call_method(
//         &mut self,
//         object: &JObject,
//         method_info: JniMethodInfo,
//         args: &[JValue],
//     ) -> JObject<'local> {
//         let (mth_name, mth_sig) = method_info;
//         let ret = self.env.call_method(object, mth_name, mth_sig, args);
//         let jobj_ret = self
//             .throw_jni_exception_if_error(ret)
//             .unwrap_or(JValueOwned::Object(JObject::null()));
//         let jobj_ret = jobj_ret.l().unwrap_or(JObject::null());
//         jobj_ret
//     }
//     pub fn call_static_method(
//         &mut self,
//         class: &str,
//         method_info: JniMethodInfo,
//         args: &[JValue],
//     ) -> JObject<'local> {
//         let class_ret = self.env.find_class(class);
//         let class: jni::objects::JClass<'local> =
//             self.throw_jni_exception_if_error(class_ret).unwrap();

//         let (mth_name, mth_sig) = method_info;
//         let ret = self.env.call_static_method(&class, mth_name, mth_sig, args);
//         let jobj_ret = self
//             .throw_jni_exception_if_error(ret)
//             .unwrap_or(JValueOwned::Object(JObject::null()));
//         let jobj_ret = jobj_ret.l().unwrap_or(JObject::null());
//         jobj_ret
//     }

//     pub fn set_field(&mut self, object: &JObject, field_info: JniFieldInfo, value: JValue) {
//         let (fld_name, fld_ty) = field_info;
//         let ret = self.env.set_field(object, fld_name, fld_ty, value);
//         self.throw_jni_exception_if_error(ret).unwrap()
//     }

//     pub fn get_field(&mut self, object: &JObject, field_info: JniFieldInfo) -> JObject<'local> {
//         let (fld_name, fld_ty) = field_info;
//         let ret = self.env.get_field(object, fld_name, fld_ty);
//         let job_ret = self.throw_jni_exception_if_error(ret).unwrap().l();
//         self.throw_jni_exception_if_error(job_ret).unwrap()
//     }
//     pub fn get_static_field(&mut self, class: &str, field_info: JniFieldInfo) -> JObject<'local> {
//         let class_ret = self.env.find_class(class);
//         let class: jni::objects::JClass<'local> =
//             self.throw_jni_exception_if_error(class_ret).unwrap();
//         let (fld_name, fld_ty) = field_info;
//         let ret = self.env.get_static_field(class, fld_name, fld_ty);
//         let job_ret = self.throw_jni_exception_if_error(ret).unwrap().l();
//         self.throw_jni_exception_if_error(job_ret).unwrap()
//     }

//     pub fn get_string(&mut self, jstring: &JObject<'local>) -> String {
//         let ret = self.env.get_string(jstring.into());
//         self.throw_jni_exception_if_error(ret).unwrap().into()
//     }

//     pub fn get_boolean(&mut self, object: &JObject<'local>) -> jboolean {
//         let ret = JValue::Object(&object).z();
//         let ret = self.throw_jni_exception_if_error(ret).unwrap();
//         if ret { JNI_TRUE } else { JNI_FALSE }
//     }

//     pub fn get_class_name(&mut self, object: &JObject<'local>) -> String {
//         let class_obj = self.call_method(object, jni_methods::GET_CLASS, &[]);
//         let name: JString = self
//             .call_method(&class_obj, jni_methods::GET_NAME, &[])
//             .into();
//         self.get_string(&name)
//     }

//     pub fn throw_java_exception_if_error<T, E: Display>(
//         &mut self,
//         error: Result<T, E>,
//     ) -> Option<T> {
//         if let Err(e) = &error {
//             eprintln!("Throwing Java exception: {}", e);
//             self.env
//                 .throw_new("java/lang/Exception", e.to_string().as_str())
//                 .ok()?;
//             return None;
//         }
//         Some(error.ok()).flatten()
//     }

//     pub fn throw_java_exception_msg(&self, env: &mut Env, msg: &str) {
//         env.throw_new(jni_str!("java/lang/Exception"), msg.into())
//             .ok();
//     }

//     pub fn throw_jni_exception_if_error<T>(
//         &mut self,
//         env: &mut Env,
//         result: errors::Result<T>,
//     ) -> Option<T> {
//         if result.is_err() {
//             if let Some(msg) = self.check_jni_exception() {
//                 eprintln!("throw_jni_exception_if_error: {}", msg);
//                 self.throw_java_exception_msg(env, &msg);
//                 return None;
//             }
//             eprintln!("throw_jni_exception_if_error: Unknown JNI Exception");
//             self.throw_java_exception_msg(env, "Unknown JNI Exception");
//             return None;
//         }
//         result.ok()
//     }

//     /// 检查并处理JNI异常
//     pub fn check_jni_exception(&mut self, env: &mut Env) -> Option<String> {
//         if env.exception_check() {
//             // 获取当前异常
//             let exception = env.exception_occurred()?;
//             if exception.is_null() {
//                 return None;
//             }

//             // 打印异常信息到Java控制台
//             env.exception_describe();

//             // 清除异常状态，避免影响后续操作
//             env.exception_clear();

//             // 获取异常消息
//             let message = match env.call_method(
//                 exception,
//                 jni_str!("getMessage"),
//                 jni_sig!(()-> java.lang.String), // ()Ljava/lang/String;
//                 &[],
//             ) {
//                 Ok(msg) => {
//                     let msg_obj: JObject = msg.l().ok()?;
//                     // JString::from(&msg_obj).to_string()
//                     JString::from(&msg_obj).to_string()
//                 }
//                 Err(_) => "未知错误".to_string(),
//             };

//             Some(message)
//         } else {
//             None
//         }
//     }

//     pub fn new_global_ref<'other_local, O>(&mut self, obj: O) -> Global<O::GlobalKind>
//     where
//         O: Reference + AsRef<JObject<'other_local>>,
//     {
//         self.throw_jni_exception_if_error(self.env.new_global_ref(obj))
//             .unwrap()
//     }

//     pub fn delete_local_ref<'other_local, O>(&mut self, obj: O)
//     where
//         O: Into<JObject<'other_local>>,
//     {
//         self.env.delete_local_ref(obj);
//     }

//     pub fn get_java_vm(&mut self) -> JavaVM {
//         self.throw_jni_exception_if_error(self.env.get_java_vm())
//             .unwrap()
//     }

//     pub fn get_array_length<'other_local, 'array>(
//         &mut self,
//         array: &'array impl AsJArrayRaw<'other_local>,
//     ) -> jsize {
//         self.throw_jni_exception_if_error(self.env.get_array_length(array))
//             .unwrap()
//     }

//     pub fn get_byte_array_region<'other_local>(
//         &mut self,
//         array: impl AsRef<JByteArray<'other_local>>,
//         start: jsize,
//         buf: &mut [jbyte],
//     ) {
//         self.throw_jni_exception_if_error(self.env.get_byte_array_region(array, start, buf))
//             .unwrap()
//     }

//     pub fn get_class<'other_local, O>(&mut self, obj: O) -> JObject<'local>
//     where
//         O: Into<JObject<'other_local>>,
//     {
//         self.call_method(&obj.into(), jni_methods::GET_CLASS, &[])
//     }

//     pub fn get_name<'other_local, O>(&mut self, obj: O) -> JObject<'local>
//     where
//         O: Into<JObject<'other_local>>,
//     {
//         self.call_method(&obj.into(), jni_methods::GET_NAME, &[])
//     }
// }

// impl<'local> Clone for JniHandler<'local> {
//     fn clone(&self) -> Self {
//         Self {
//             env: unsafe { self.env.unsafe_clone() },
//         }
//     }
// }
