use jni::signature::ReturnType;
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
use jni::JNIEnv;
use jni::objects::*;

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInit(
    mut env: JNIEnv,
    _this: JObject,
    databasePath: JString,
) {
    let db = env
        .get_string(&databasePath)
        .expect("Can't get java string");
    let db_str: String = db.into();
    println!("JniWebView::PluginManager.init() {}", db_str);

    let obj_class = env.get_object_class(_this).unwrap();
    let ret: JValueGen<JObject<'_>> = env
        .call_method(&obj_class, "getName", "()Ljava/lang/String;", &[])
        .unwrap();

    println!("type name: {}", ret.type_name());

    match ret {
        JValueGen::Object(o) => {
            let o1 = JString::from(o);
            let name = env.get_string(&o1).unwrap();
            let name_str: String = name.into();
            println!("name: {}", name_str);
        }
        JValueGen::Byte(_) => todo!(),
        JValueGen::Char(_) => todo!(),
        JValueGen::Short(_) => todo!(),
        JValueGen::Int(_) => todo!(),
        JValueGen::Long(_) => todo!(),
        JValueGen::Bool(_) => todo!(),
        JValueGen::Float(_) => todo!(),
        JValueGen::Double(_) => todo!(),
        JValueGen::Void => todo!(),
    }

    env.delete_local_ref(databasePath).unwrap();
    env.delete_local_ref(obj_class).unwrap();
}
