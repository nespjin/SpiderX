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

use crate::jni::jni_utils::JniConstructorInfo;

pub const LIST: &str = "java/util/List";
pub const ARRAY_LIST: &str = "java/util/ArrayList";
pub const ARRAY_LIST_CONSTOR: JniConstructorInfo = (ARRAY_LIST, "()V");

pub const MAP: &str = "java/util/Map";
pub const HASH_MAP: &str = "java/util/HashMap";
pub const HASH_MAP_CONSTOR: JniConstructorInfo = (HASH_MAP, "()V");

pub const BOOLEAN: &str = "java/lang/Boolean";
pub const BOOLEAN_CONSTOR: JniConstructorInfo = (BOOLEAN, "(Z)V");

pub const LONG: &str = "java/lang/Long";
pub const LONG_CONSTOR: JniConstructorInfo = (LONG, "(J)V");

pub const DOUBLE: &str = "java/lang/Double";
pub const DOUBLE_CONSTOR: JniConstructorInfo = (LONG, "(D)V");
