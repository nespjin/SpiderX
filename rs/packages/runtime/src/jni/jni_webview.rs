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

use std::{
    collections::HashMap,
    sync::{Arc, MutexGuard, RwLock},
};

use jni::{
    JNIEnv,
    objects::{GlobalRef, JClass, JObject, JString, JValueGen},
    sys::jint,
};

use crate::{
    jni::jni_plugin_manager::JniPluginManager,
    web_engine::web_engine::{WebEngine, WebEngineListener},
};

const JAVA_CLASS_NAME_WV: &'static str = "com.nesp.spiderx.runtime.JniWebView";

pub const JNI_WV_JAVA_FIELD_NAME_PTR: &'static str = "mPtr";

const JAVA_METHOD_INFO_INIT: &'static [&'static str; 2] = &["init", "()V"];
const JAVA_METHOD_INFO_LOAD_URL: &'static [&'static str; 2] = &["loadUrl", "(Ljava/lang/String;)V"];
const JAVA_METHOD_INFO_LOAD_DATA: &'static [&'static str; 2] =
    &["loadData", "(Ljava/lang/String;)V"];
const JAVA_METHOD_INFO_LOAD_RELOAD: &'static [&'static str; 2] = &["reload", "()V"];
const JAVA_METHOD_INFO_LOAD_EVALUATE: &'static [&'static str; 2] =
    &["evaluate", "(Ljava/lang/String;)Ljava/lang/String;"];
const JAVA_METHOD_INFO_ADD_LISTENER: &'static [&'static str; 2] = &["addListener", "(I)V"];
const JAVA_METHOD_INFO_REMOVE_LISTENER: &'static [&'static str; 2] = &["removeListener", "(I)V"];

pub struct JniWebView {
    wv_java_obj: GlobalRef,
    id: i64,
    listener_id: i64,
    listeners: Arc<RwLock<HashMap<i64, Arc<dyn WebEngineListener>>>>,
}

impl JniWebView {
    pub fn new(id: i64, wv_java_obj: GlobalRef) -> Self {
        JniWebView {
            wv_java_obj,
            id,
            listener_id: 0,
            listeners: Arc::new(RwLock::new(HashMap::new())),
        }
    }
}

impl WebEngine for JniWebView {
    fn init(&mut self) -> Result<(), String> {
        let jni_plg_mgr = JniPluginManager::get_instance();
        let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

        let [name, sig] = JAVA_METHOD_INFO_INIT;

        jni_plg_mgr
            .java_env()?
            .call_method(&self.wv_java_obj, name, sig, &[])
            .map_err(|e| e.to_string())?;

        Ok(())
    }

    fn load_url(&self, url: &str) -> Result<(), String> {
        todo!()
    }

    fn load_data(&self, data: &str) -> Result<(), String> {
        todo!()
    }

    fn reload(&self) -> Result<(), String> {
        todo!()
    }

    fn evaluate(&self, script: &str) -> Result<String, String> {
        todo!()
    }

    fn add_listener(&mut self, listener: Arc<dyn WebEngineListener>) -> i64 {
        let id = self.listener_id;
        self.listener_id += 1;
        self.listeners.write().unwrap().insert(id, listener);
        id
    }

    fn remove_listener(&mut self, id: i64) {
        self.listeners.write().unwrap().remove(&id);
        // self.listeners
        //     .write()
        //     .unwrap()
        //     .retain(|l| !std::ptr::addr_eq(l.as_ref(), listener.as_ref()));
    }

    fn notify_listeners<F>(&self, mut callback: F)
    where
        F: FnMut(Arc<dyn WebEngineListener>),
    {
        self.listeners
            .read()
            .unwrap()
            .values()
            .for_each(|l| callback(l.clone()));
    }

    fn listeners(&self) -> Vec<Arc<dyn WebEngineListener>> {
        self.listeners
            .read()
            .unwrap()
            .values()
            .cloned()
            .collect::<Vec<_>>()
    }

    fn destroy(&mut self) -> Result<(), String> {
        todo!()
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeOnPageStartedListener<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    progress: jint,
) {
    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    // let listeners = jni_wv.listeners();
    // listeners.iter().for_each(|listener| {
    //     listener.on_load_progress(jni_wv.clone(), 100);
    // });

    jni_wv.notify_listeners(|listener| {
        listener.on_load_progress(jni_wv.clone(), progress);
    });
}

fn get_jni_wv_from_java_obj<'other_local, O>(env: &mut JNIEnv, obj: O) -> Arc<JniWebView>
where
    O: AsRef<JObject<'other_local>>,
{
    let ptr = env.get_field(obj, JNI_WV_JAVA_FIELD_NAME_PTR, "J").unwrap();

    let wv_id = match ptr {
        JValueGen::Long(ptr) => Some(ptr),
        _ => None,
    }
    .unwrap();

    let jni_plg_mgr = JniPluginManager::get_instance();
    let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

    let jni_wv = jni_plg_mgr.get_jni_wv(wv_id).unwrap();
    jni_wv
}
