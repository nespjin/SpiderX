mod frb_generated; /* AUTO INJECTED BY flutter_rust_bridge. This line may not be accurate, and you can change it according to your needs. */
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

// #![no_std]
// #![crate_type = "staticlib"]
// #![crate_type = "cdylib"]

#[cfg(not(any(
    target_os = "android",
    target_os = "windows",
    target_os = "macos",
    target_os = "linux"
)))]
compile_error!("Only Android, Windows, MacOS, Linux are supported");

pub(crate) mod database;
pub(crate) mod device;
pub(crate) mod dsl_engine;
pub(crate) mod executor;
pub(crate) mod plugin_manager;
pub(crate) mod repository;
pub(crate) mod utils;
pub(crate) mod web_engine;

// #[cfg(any(target_os = "android", feature = "jni"))]
#[allow(non_snake_case)]
pub(crate) mod jni;

pub(crate) mod dart;
