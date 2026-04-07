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

pub(crate) mod bind_jni_plugin_manager;
pub(crate) mod jni_dataset;
pub(crate) mod jni_json;
pub(crate) mod jni_log;
pub(crate) mod jni_plugin;
pub(crate) mod jni_plugin_manager;
pub(crate) mod jni_screen_type;
pub(crate) mod jni_utils;
pub(crate) mod jni_webview;

// #[unsafe(no_mangle)]
// pub extern "system" fn JNI_OnLoad(
//     vm: jni::JavaVM,
//     _reserved: *mut std::ffi::c_void,
// ) -> jni::sys::jint {
//     let jni_version = vm
//         .attach_current_thread(|env| env.version())
//         .expect("Failed to get jni version");
//     let jni_version_int: jni::sys::jint = jni_version.into();
//     log::info!("JNI_OnLoad: {}", jni_version_int);
//     jni_version_int
// }
