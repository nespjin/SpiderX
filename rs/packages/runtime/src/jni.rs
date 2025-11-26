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
    JNIVersion,
    sys::{JNI_VERSION_1_1, JNI_VERSION_1_2, JNI_VERSION_1_4, JNI_VERSION_1_6, JNI_VERSION_1_8},
};

use crate::jni::jni_handler::JVM;

pub(crate) mod jni_classes;
pub(crate) mod jni_dataset;
pub(crate) mod jni_handler;
pub(crate) mod jni_json;
pub(crate) mod jni_list;
pub(crate) mod jni_log;
pub(crate) mod jni_map;
pub(crate) mod jni_methods;
pub(crate) mod jni_object;
pub(crate) mod jni_object_owned;
pub(crate) mod jni_plugin;
pub(crate) mod jni_plugin_manager;
pub(crate) mod jni_screen_type;
pub(crate) mod jni_thread;
pub(crate) mod jni_webview;

#[unsafe(no_mangle)]
pub extern "system" fn JNI_OnLoad(
    vm: jni::JavaVM,
    _reserved: *mut std::ffi::c_void,
) -> jni::sys::jint {
    let jni_version = vm
        .get_env()
        .expect("Failed to create JNIEnv")
        .get_version()
        .expect("Failed to get JNI version");
    let jni_version_int = match jni_version {
        JNIVersion::V1 => JNI_VERSION_1_1,
        JNIVersion::V2 => JNI_VERSION_1_2,
        JNIVersion::V4 => JNI_VERSION_1_4,
        JNIVersion::V6 => JNI_VERSION_1_6,
        JNIVersion::V8 => JNI_VERSION_1_8,
        JNIVersion::Invalid(v) => v,
    };
    log::info!("JNI_OnLoad: {}", jni_version_int);
    JVM.set(vm).expect("Failed to set global JavaVM");
    jni_version_int
}
