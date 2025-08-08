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

use std::sync::{Arc, MutexGuard};

use jni::{
    JNIEnv,
    objects::{GlobalRef, JObject, JString, JValueGen},
    sys::{jboolean, jint, jstring},
};

use crate::{
    jni::{jni_plugin_manager::JniPluginManager, jni_utils},
    web_engine::web_engine::{WebEngine, WebEngineListener},
};

const JAVA_CLASS_NAME_WV: &'static str = "com.nesp.spiderx.runtime.JniWebView";

pub const JNI_WV_JAVA_FIELD_NAME_PTR: &'static str = "mPtr";

const JAVA_METHOD_INFO_INIT: &'static [&'static str; 2] = &["init", "()V"];
const JAVA_METHOD_INFO_LOAD_URL: &'static [&'static str; 2] = &["loadUrl", "(Ljava/lang/String;)V"];
const JAVA_METHOD_INFO_LOAD_DATA: &'static [&'static str; 2] =
    &["loadData", "(Ljava/lang/String;)V"];
const JAVA_METHOD_INFO_RELOAD: &'static [&'static str; 2] = &["reload", "()V"];
const JAVA_METHOD_INFO_EVALUATE: &'static [&'static str; 2] =
    &["evaluate", "(Ljava/lang/String;)Ljava/lang/String;"];
const JAVA_METHOD_INFO_DESTROY: &'static [&'static str; 2] = &["destroy", "()V"];

pub struct JniWebView {
    wv_java_obj: GlobalRef,
    id: i64,
    // listener_id: i64,
    // listeners: Arc<RwLock<HashMap<i64, Arc<dyn WebEngineListener>>>>,
    listener: Option<Arc<dyn WebEngineListener>>,
}

impl JniWebView {
    pub fn new(id: i64, wv_java_obj: GlobalRef) -> Self {
        JniWebView {
            wv_java_obj,
            id,
            // listener_id: 0,
            // listeners: Arc::new(RwLock::new(HashMap::new())),
            listener: None,
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
        let jni_plg_mgr = JniPluginManager::get_instance();
        let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

        let [name, sig] = JAVA_METHOD_INFO_LOAD_URL;

        let j_url = jni_plg_mgr
            .java_env()?
            .new_string(url)
            .map_err(|e| e.to_string())?;

        jni_plg_mgr
            .java_env()?
            .call_method(&self.wv_java_obj, name, sig, &[JValueGen::Object(&j_url)])
            .map_err(|e| e.to_string())?;

        Ok(())
    }

    fn load_data(&self, data: &str) -> Result<(), String> {
        let jni_plg_mgr = JniPluginManager::get_instance();
        let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

        let [name, sig] = JAVA_METHOD_INFO_LOAD_DATA;

        let j_data = jni_plg_mgr
            .java_env()?
            .new_string(data)
            .map_err(|e| e.to_string())?;

        jni_plg_mgr
            .java_env()?
            .call_method(&self.wv_java_obj, name, sig, &[JValueGen::Object(&j_data)])
            .map_err(|e| e.to_string())?;

        Ok(())
    }

    fn reload(&self) -> Result<(), String> {
        let jni_plg_mgr = JniPluginManager::get_instance();
        let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

        let [name, sig] = JAVA_METHOD_INFO_RELOAD;

        jni_plg_mgr
            .java_env()?
            .call_method(&self.wv_java_obj, name, sig, &[])
            .map_err(|e| e.to_string())?;

        Ok(())
    }

    fn evaluate(&self, script: &str) -> Result<String, String> {
        let jni_plg_mgr = JniPluginManager::get_instance();
        let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

        let [name, sig] = JAVA_METHOD_INFO_EVALUATE;

        let j_script = jni_plg_mgr
            .java_env()?
            .new_string(script)
            .map_err(|e| e.to_string())?;

        let ret = jni_plg_mgr
            .java_env()?
            .call_method(
                &self.wv_java_obj,
                name,
                sig,
                &[JValueGen::Object(&j_script)],
            )
            .map_err(|e| e.to_string())?;

        let ret = match ret {
            JValueGen::Object(j_ret) => jni_plg_mgr
                .java_env()?
                .get_string(&JString::from(j_ret))
                .map_err(|e| e.to_string())?
                .into(),
            _ => return Err("ret is not a string".to_string()),
        };

        Ok(ret)
    }

    // fn add_listener(&mut self, listener: Arc<dyn WebEngineListener>) -> i64 {
    //     let id = self.listener_id;
    //     self.listener_id += 1;
    //     self.listeners.write().unwrap().insert(id, listener);
    //     id
    // }

    // fn remove_listener(&mut self, id: i64) {
    //     self.listeners.write().unwrap().remove(&id);
    //     // self.listeners
    //     //     .write()
    //     //     .unwrap()
    //     //     .retain(|l| !std::ptr::addr_eq(l.as_ref(), listener.as_ref()));
    // }

    // fn notify_listeners<F>(&self, mut callback: F)
    // where
    //     F: FnMut(Arc<dyn WebEngineListener>),
    // {
    //     self.listeners
    //         .read()
    //         .unwrap()
    //         .values()
    //         .for_each(|l| callback(l.clone()));
    // }

    // fn listeners(&self) -> Vec<Arc<dyn WebEngineListener>> {
    //     self.listeners
    //         .read()
    //         .unwrap()
    //         .values()
    //         .cloned()
    //         .collect::<Vec<_>>()
    // }

    fn set_listener(&mut self, listener: Arc<dyn WebEngineListener>) {
        if self.listener.is_some() {
            return;
        }
        self.listener = Some(listener);
    }

    fn listener(&self) -> Option<Arc<dyn WebEngineListener>> {
        self.listener.clone()
    }

    fn destroy(&mut self) -> Result<(), String> {
        let jni_plg_mgr = JniPluginManager::get_instance();
        let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

        let [name, sig] = JAVA_METHOD_INFO_DESTROY;

        jni_plg_mgr
            .java_env()?
            .call_method(&self.wv_java_obj, name, sig, &[])
            .map_err(|e| e.to_string())?;

        Ok(())
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageStarted<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) {
    let url = env.get_string(&url).map_err(|e| e.to_string());
    let url = match jni_utils::throw_java_expception_if_error(&mut env, url) {
        Some(path) => String::from(path),
        None => return,
    };

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    // let listeners = jni_wv.listeners();
    // listeners.iter().for_each(|listener| {
    //     listener.on_load_progress(jni_wv.clone(), 100);
    // });

    // jni_wv.notify_listeners(|listener| {
    //     listener.on_page_started(jni_wv.clone(), &url);
    // });
    jni_wv
        .listener()
        .map(|l| l.on_page_started(jni_wv.clone(), &url));
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageFinished<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) {
    let url = env.get_string(&url).map_err(|e| e.to_string());
    let url = match jni_utils::throw_java_expception_if_error(&mut env, url) {
        Some(path) => String::from(path),
        None => return,
    };

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);
    jni_wv
        .listener()
        .map(|l| l.on_page_finished(jni_wv.clone(), &url));
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageError<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) {
    let url = env.get_string(&url).map_err(|e| e.to_string());
    let url = match jni_utils::throw_java_expception_if_error(&mut env, url) {
        Some(path) => String::from(path),
        None => return,
    };

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv
        .listener()
        .map(|l| l.on_page_error(jni_wv.clone(), &url));
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnLoadProgress<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    progress: jint,
) {
    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv
        .listener()
        .map(|l| l.on_load_progress(jni_wv.clone(), progress));
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnShouldOverrideUrlLoading<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) -> jboolean {
    let url = env.get_string(&url).map_err(|e| e.to_string());
    let url = match jni_utils::throw_java_expception_if_error(&mut env, url) {
        Some(path) => String::from(path),
        None => return 0,
    };

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv
        .listener()
        .map(|l| l.should_override_url_loading(jni_wv.clone(), &url))
        .map(|b| if b { 1 } else { 0 })
        .unwrap_or(0)
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnShouldInterceptRequest<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) -> jstring {
    let url = env.get_string(&url).map_err(|e| e.to_string());
    let url = match jni_utils::throw_java_expception_if_error(&mut env, url) {
        Some(path) => String::from(path),
        None => return std::ptr::null_mut(),
    };

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv
        .listener()
        .map(|l| l.should_intercept_request(jni_wv.clone(), &url))
        .map(|s| env.new_string(s))
        .map(|e| e.map_err(|e| e.to_string()))
        .map(|e| jni_utils::throw_java_expception_if_error(&mut env, e))
        .map(|e| e.map(|e| e.into_raw()))
        .map(|e| e.unwrap_or(std::ptr::null_mut()))
        .unwrap_or(std::ptr::null_mut())
}

fn get_jni_wv_from_java_obj<'other_local, O>(env: &mut JNIEnv, obj: O) -> Arc<JniWebView>
where
    O: AsRef<JObject<'other_local>>,
{
    let ptr = env.get_field(obj, JNI_WV_JAVA_FIELD_NAME_PTR, "J").unwrap();

    let wv_id = match ptr {
        JValueGen::Long(ptr) => Some(ptr),
        _ => None,
    }.expect("wv_id is none in java object");

    let jni_plg_mgr = JniPluginManager::get_instance();
    let jni_plg_mgr: MutexGuard<'_, JniPluginManager> = jni_plg_mgr.lock().unwrap();

     jni_plg_mgr.get_jni_wv(wv_id)
        .expect("jni_wv is none in jni_plg_mgr")
        .expect("jni_wv is none in jni_plg_mgr")

}
