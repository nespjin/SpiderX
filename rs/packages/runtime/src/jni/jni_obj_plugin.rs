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

use core::data::{plugin::Dataset, screen_type::ScreenType};

use jni::{
    JNIEnv,
    objects::{JObject, JValueGen},
    sys::{JNI_FALSE, JNI_TRUE, jobject},
};

use crate::jni::{
    jni_constants::{
        JAVA_CLASS_NAME_ARRAY_LIST, JAVA_CLASS_NAME_HASH_MAP, JAVA_CLASS_NAME_LIST,
        JAVA_CLASS_NAME_MAP, JAVA_METHOD_NAME_LIST_ADD, JAVA_METHOD_NAME_MAP_PUT,
        JAVA_METHOD_SIG_LIST_ADD, JAVA_METHOD_SIG_MAP_PUT,
    },
    jni_obj_dataset::JAVA_CLASS_NAME_DATASET,
    jni_obj_screen_type::{self, JAVA_CLASS_NAME_SCREEN_TYPE},
};

const JAVA_CLASS_NAME_PLUGIN: &'static str = "com/nesp/spiderx/runtime/model/Plugin";

pub fn new(
    env: &mut JNIEnv,
    id: &str,
    name: &str,
    author: &Option<String>,
    version: &str,
    runtime_version: &str,
    description: &Option<String>,
    tags: &Vec<String>,
    supported_screen_types: &[ScreenType],
    datasets: &[Dataset],
) -> Result<jobject, String> {
    let class = env
        .find_class(JAVA_CLASS_NAME_PLUGIN)
        .expect("Plugin class not found!");
    let obj = env
        .new_object(class, "()V", &[])
        .expect("Cant new Plugin instance!");

    let id = env.new_string(id).expect("Cant new id String!");
    env.set_field(&obj, "id", "Ljava/lang/String;", JValueGen::Object(&id))
        .expect("cant new id");
    env.delete_local_ref(id).unwrap();

    let name = env.new_string(name).expect("Cant new name String!");
    env.set_field(&obj, "name", "Ljava/lang/String;", JValueGen::Object(&name))
        .unwrap();
    env.delete_local_ref(name).unwrap();

    let author = env
        .new_string(author.clone().unwrap_or("".to_string()))
        .expect("Cant new author String!");
    env.set_field(
        &obj,
        "author",
        "Ljava/lang/String;",
        JValueGen::Object(&author),
    )
    .unwrap();
    env.delete_local_ref(author).unwrap();

    let version = env.new_string(version).expect("Cant new version String!");
    env.set_field(
        &obj,
        "version",
        "Ljava/lang/String;",
        JValueGen::Object(&version),
    )
    .unwrap();
    env.delete_local_ref(version).unwrap();

    let runtime_version = env
        .new_string(runtime_version)
        .expect("Cant new runtimeVersion String!");
    env.set_field(
        &obj,
        "runtimeVersion",
        "Ljava/lang/String;",
        JValueGen::Object(&runtime_version),
    )
    .unwrap();
    env.delete_local_ref(runtime_version).unwrap();

    let description = env
        .new_string(description.clone().unwrap_or("".to_string()))
        .expect("Cant new description String!");
    env.set_field(
        &obj,
        "description",
        "Ljava/lang/String;",
        JValueGen::Object(&description),
    )
    .unwrap();
    env.delete_local_ref(description).unwrap();

    let arr_list_cls = env
        .find_class(JAVA_CLASS_NAME_ARRAY_LIST)
        .expect("Cant find class ArrayList!");

    let tags_arr_list = env
        .new_object(arr_list_cls, "()V", &[])
        .expect("Cant new array list object!");

    for tag in tags {
        let tag_str = env.new_string(tag).expect("Cant create string object!");
        env.call_method(
            &tags_arr_list,
            JAVA_METHOD_NAME_LIST_ADD,
            JAVA_METHOD_SIG_LIST_ADD,
            &[JValueGen::Object(&tag_str)],
        )
        .expect("Cant add tag to array list!");
        env.delete_local_ref(tag_str).unwrap();
    }

    env.set_field(
        &obj,
        "tags",
        &format!("L{};", JAVA_CLASS_NAME_LIST),
        JValueGen::Object(&tags_arr_list),
    )
    .unwrap();
    env.delete_local_ref(tags_arr_list).unwrap();

    let arr_list_cls = env
        .find_class(JAVA_CLASS_NAME_ARRAY_LIST)
        .expect("Cant find class ArrayList!");

    let screen_type_arr_list = env
        .new_object(arr_list_cls, "()V", &[])
        .expect("Cant new array list object!");

    for screen_type in supported_screen_types {
        let screen_type_cls = env
            .find_class(JAVA_CLASS_NAME_SCREEN_TYPE)
            .expect("ScreenType class not found!");
        let screen_type_obj = env
            .get_static_field(
                &screen_type_cls,
                jni_obj_screen_type::get_enum_value_name(screen_type),
                &format!("L{};", JAVA_CLASS_NAME_SCREEN_TYPE),
            )
            .unwrap()
            .l()
            .unwrap();

        env.call_method(
            &screen_type_arr_list,
            JAVA_METHOD_NAME_LIST_ADD,
            JAVA_METHOD_SIG_LIST_ADD,
            &[JValueGen::Object(&screen_type_obj)],
        )
        .unwrap();
        env.delete_local_ref(screen_type_obj).unwrap();
    }

    env.set_field(
        &obj,
        "supportedScreenTypes",
        &format!("L{};", JAVA_CLASS_NAME_LIST),
        JValueGen::Object(&screen_type_arr_list),
    )
    .unwrap();
    env.delete_local_ref(screen_type_arr_list).unwrap();

    let arr_list_cls = env
        .find_class(JAVA_CLASS_NAME_ARRAY_LIST)
        .expect("Cant find class ArrayList!");

    let dataset_arr_list = env
        .new_object(arr_list_cls, "()V", &[])
        .expect("Cant new array list object!");

    for dataset in datasets {
        let dataset_cls = env
            .find_class(JAVA_CLASS_NAME_DATASET)
            .expect("Dataset class not found!");
        let dataset_obj = env
            .new_object(dataset_cls, "()V", &[])
            .expect("Dataset object alloc failed!");

        let id = env.new_string(&dataset.id).expect("Cant new id String!");
        env.set_field(
            &dataset_obj,
            "id",
            "Ljava/lang/String;",
            JValueGen::Object(&id),
        )
        .unwrap();
        env.delete_local_ref(id).unwrap();

        let url = env.new_string(&dataset.url).expect("Cant new url String!");
        env.set_field(
            &dataset_obj,
            "url",
            "Ljava/lang/String;",
            JValueGen::Object(&url),
        )
        .unwrap();
        env.delete_local_ref(url).unwrap();

        let url_compact = env
            .new_string(&dataset.url_compact.clone().unwrap_or("".to_string()))
            .expect("Cant new urlCompact String!");
        env.set_field(
            &dataset_obj,
            "urlCompact",
            "Ljava/lang/String;",
            JValueGen::Object(&url_compact),
        )
        .unwrap();
        env.delete_local_ref(url_compact).unwrap();

        let url_medium = env
            .new_string(&dataset.url_medium.clone().unwrap_or("".to_string()))
            .expect("Cant new urlMedium String!");
        env.set_field(
            &dataset_obj,
            "urlMedium",
            "Ljava/lang/String;",
            JValueGen::Object(&url_medium),
        )
        .unwrap();
        env.delete_local_ref(url_medium).unwrap();

        let url_expanded = env
            .new_string(&dataset.url_expanded.clone().unwrap_or("".to_string()))
            .expect("Cant new urlExpanded String!");
        env.set_field(
            &dataset_obj,
            "urlExpanded",
            "Ljava/lang/String;",
            JValueGen::Object(&url_expanded),
        )
        .unwrap();
        env.delete_local_ref(url_expanded).unwrap();

        let js = env
            .new_string(&dataset.js.clone().unwrap_or("".to_string()))
            .expect("Cant new js String!");
        env.set_field(&dataset_obj, "js", "Ljava/lang/String;", JValueGen::Object(&js))
            .unwrap();
        env.delete_local_ref(js).unwrap();

        let js_compact = env
            .new_string(&dataset.js_compact.clone().unwrap_or("".to_string()))
            .expect("Cant new jsCompact String!");
        env.set_field(
            &dataset_obj,
            "jsCompact",
            "Ljava/lang/String;",
            JValueGen::Object(&js_compact),
        )
        .unwrap();
        env.delete_local_ref(js_compact).unwrap();

        let js_medium = env
            .new_string(&dataset.js_medium.clone().unwrap_or("".to_string()))
            .expect("Cant new jsMedium String!");
        env.set_field(
            &dataset_obj,
            "jsMedium",
            "Ljava/lang/String;",
            JValueGen::Object(&js_medium),
        )
        .unwrap();
        env.delete_local_ref(js_medium).unwrap();

        let js_expanded = env
            .new_string(&dataset.js_expanded.clone().unwrap_or("".to_string()))
            .expect("Cant new jsExpanded String!");
        env.set_field(
            &dataset_obj,
            "jsExpanded",
            "Ljava/lang/String;",
            JValueGen::Object(&js_expanded),
        )
        .unwrap();
        env.delete_local_ref(js_expanded).unwrap();

        let dsl_obj = match &dataset.dsl {
            Some(dsl) => json_value_to_hash_map(env, dsl),
            None => JObject::null().into_raw(),
        };
        let dsl_obj = unsafe { JObject::from_raw(dsl_obj) };
        env.set_field(
            &dataset_obj,
            "dsl",
            format!("L{};", JAVA_CLASS_NAME_MAP),
            JValueGen::Object(&dsl_obj),
        )
        .unwrap();
        env.delete_local_ref(dsl_obj).unwrap();

        let dsl_compact = match &dataset.dsl_compact {
            Some(dsl) => json_value_to_hash_map(env, dsl),
            None => JObject::null().into_raw(),
        };
        let dsl_compact = unsafe { JObject::from_raw(dsl_compact) };
        env.set_field(
            &dataset_obj,
            "dslCompact",
            format!("L{};", JAVA_CLASS_NAME_MAP),
            JValueGen::Object(&dsl_compact),
        )
        .unwrap();
        env.delete_local_ref(dsl_compact).unwrap();

        let dsl_medium = match &dataset.dsl_medium {
            Some(dsl) => json_value_to_hash_map(env, dsl),
            None => JObject::null().into_raw(),
        };
        let dsl_medium = unsafe { JObject::from_raw(dsl_medium) };
        env.set_field(
            &dataset_obj,
            "dslMedium",
            format!("L{};", JAVA_CLASS_NAME_MAP),
            JValueGen::Object(&dsl_medium),
        )
        .unwrap();
        env.delete_local_ref(dsl_medium).unwrap();

        let dsl_expanded = match &dataset.dsl_expanded {
            Some(dsl) => json_value_to_hash_map(env, dsl),
            None => JObject::null().into_raw(),
        };
        let dsl_expanded = unsafe { JObject::from_raw(dsl_expanded) };
        env.set_field(
            &dataset_obj,
            "dslExpanded",
            format!("L{};", JAVA_CLASS_NAME_MAP),
            JValueGen::Object(&dsl_expanded),
        )
        .unwrap();
        env.delete_local_ref(dsl_expanded).unwrap();

        env.call_method(
            &dataset_arr_list,
            JAVA_METHOD_NAME_LIST_ADD,
            JAVA_METHOD_SIG_LIST_ADD,
            &[JValueGen::Object(&dataset_obj)],
        )
        .unwrap();
    }

    env.set_field(
        &obj,
        "datasets",
        &format!("L{};", JAVA_CLASS_NAME_LIST),
        JValueGen::Object(&dataset_arr_list),
    )
    .unwrap();
    env.delete_local_ref(dataset_arr_list).unwrap();

    Ok(obj.into_raw())
}

fn json_value_to_hash_map(env: &mut JNIEnv, value: &serde_json::Value) -> jobject {
    match value {
        serde_json::Value::Object(map) => {
            let hash_map_cls = env
                .find_class(JAVA_CLASS_NAME_HASH_MAP)
                .expect("Cant find HashMap class!");
            let hash_map = env
                .new_object(hash_map_cls, "()V", &[])
                .expect("Cant new HashMap object!");
            for (key, value) in map {
                let key = env.new_string(key).expect("Can new key string");
                let value = json_value_to_obj(env, value);
                let value = unsafe { JObject::from_raw(value) };
                env.call_method(
                    &hash_map,
                    JAVA_METHOD_NAME_MAP_PUT,
                    JAVA_METHOD_SIG_MAP_PUT,
                    &[JValueGen::Object(&key), JValueGen::Object(&value)],
                )
                .expect("Cant call HashMap.put method!");
            }
            hash_map
        }
        _ => JObject::null(),
    }
    .into_raw()
}

fn json_value_to_obj(env: &mut JNIEnv, value: &serde_json::Value) -> jobject {
    let obj = match value {
        serde_json::Value::Null => JObject::null(),
        serde_json::Value::Bool(value) => env
            .new_object(
                "java/lang/Boolean",
                "(Z)V",
                &[JValueGen::Bool(if *value { JNI_TRUE } else { JNI_FALSE })],
            )
            .expect("Cant new Boolean!"),
        serde_json::Value::Number(number) => {
            if number.is_i64() || number.is_u64() {
                env.new_object(
                    "java/lang/Long",
                    "(J)V",
                    &[JValueGen::Long(number.as_i64().unwrap())],
                )
                .expect("Cant new Long!")
            } else if number.is_f64() {
                env.new_object(
                    "java/lang/Double",
                    "(D)V",
                    &[JValueGen::Double(number.as_f64().unwrap())],
                )
                .expect("Cant new Double!")
            } else {
                panic!("Unknown number type!")
            }
        }
        serde_json::Value::String(str) => {
            let str = env.new_string(str).expect("Cant new string");
            str.into()
        }
        serde_json::Value::Array(values) => {
            let arr_list = env
                .new_object(JAVA_CLASS_NAME_ARRAY_LIST, "()V", &[])
                .expect("Cant new array list");

            for value in values {
                let obj = json_value_to_obj(env, value);
                let obj = unsafe { JObject::from_raw(obj) };
                env.call_method(
                    &arr_list,
                    JAVA_METHOD_NAME_LIST_ADD,
                    JAVA_METHOD_SIG_LIST_ADD,
                    &[JValueGen::Object(&obj)],
                )
                .expect("Can to add obj");
            }
            arr_list
        }
        serde_json::Value::Object(map) => {
            let hash_map = env
                .new_object(JAVA_CLASS_NAME_HASH_MAP, "()V", &[])
                .expect("Cant new hash map");
            for (key, value) in map {
                let key = env.new_string(key).expect("cant new key string");
                let obj = json_value_to_obj(env, value);
                let obj = unsafe { JObject::from_raw(obj) };
                env.call_method(
                    &hash_map,
                    JAVA_METHOD_NAME_MAP_PUT,
                    JAVA_METHOD_SIG_MAP_PUT,
                    &[JValueGen::Object(&key), JValueGen::Object(&obj)],
                )
                .expect("Can to put obj");
            }
            hash_map
        }
    };
    obj.into_raw()
}
