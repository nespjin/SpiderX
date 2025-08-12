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

use crate::jni::jni_handler::JniMethodInfo;

pub const LIST_ADD: JniMethodInfo = ("add", "(Ljava/lang/Object;)Z");
pub const MAP_PUT: JniMethodInfo = (
    "put",
    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
);

pub const GET_CLASS: JniMethodInfo = ("getClass", "()Ljava/lang/Class;");
pub const GET_NAME: JniMethodInfo = ("getName", "()Ljava/lang/String;");
