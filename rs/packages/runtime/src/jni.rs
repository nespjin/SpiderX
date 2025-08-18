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
pub(crate) mod jni_webview;

#[unsafe(no_mangle)]
pub extern "system" fn JNI_OnLoad(
    vm: jni::JavaVM,
    _reserved: *mut std::ffi::c_void,
) -> jni::sys::jint {
    JVM.set(vm).expect("Failed to set global JavaVM");
    jni::sys::JNI_VERSION_1_8
}
