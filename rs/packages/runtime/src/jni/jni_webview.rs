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
    JNIEnv,
    objects::{GlobalRef, JObject, JString, JValueGen},
    sys::{JNI_FALSE, jboolean, jint, jstring},
};
use once_cell::sync::OnceCell;

use crate::{
    jni::jni_handler::{JniFieldInfo, JniHandler, JniMethodInfo},
    web_engine::{
        web_engine::{WebEngine, WebEngineListenerMut, WebEngineMut},
        web_engine_manager::WebEngineManager,
    },
};

// const JAVA_CLASS_NAME_WV: &'static str = "com/nesp/spiderx/runtime/JniWebView";

pub const FIELD_PTR: JniFieldInfo = ("mPtr", "J");

const METHOD_INIT: JniMethodInfo = ("init", "()V");
const METHOD_LOAD_URL: JniMethodInfo = ("loadUrl", "(Ljava/lang/String;)V");
const METHOD_LOAD_DATA: JniMethodInfo = ("loadData", "(Ljava/lang/String;)V");
const METHOD_RELOAD: JniMethodInfo = ("reload", "()V");
const METHOD_EVALUATE: JniMethodInfo = ("evaluate", "(Ljava/lang/String;)Ljava/lang/String;");
const METHOD_DESTROY: JniMethodInfo = ("destroy", "()V");

pub const JNI_WV_JAVA_FIELD_NAME_PTR: &'static str = "mPtr";

pub static JAVA_WEBVIEW_CLASS: OnceCell<GlobalRef> = OnceCell::new();
const JAVA_WV_CTOR_SIG: &'static str = "()V";

pub fn get_java_webview_class() -> Result<GlobalRef, String> {
    JAVA_WEBVIEW_CLASS
        .get()
        .ok_or_else(|| "Java WebView class not initialized".to_string())
        .cloned()
}

pub fn new_webview_obj<'local>() -> Result<JObject<'local>, String> {
    let wv_class = &get_java_webview_class()?;
    let mut handler = JniHandler::new();
    let wv_obj = handler.new_object_with_class(wv_class, JAVA_WV_CTOR_SIG, &[]);
    Ok(wv_obj)
}
pub fn new_jni_webview(id: i64) -> Result<JniWebView, String> {
    let mut jni_handler = JniHandler::new();
    let webview_obj = jni_handler.new_global_ref(new_webview_obj()?);
    jni_handler.set_field(&webview_obj, FIELD_PTR, JValueGen::Long(id));
    Ok(JniWebView::new(id, webview_obj))
}

pub struct JniWebView {
    webview_java_obj: GlobalRef,
    id: i64,
    // listener_id: i64,
    // listeners: Arc<RwLock<HashMap<i64, WebEngineListenerMut>>>,
    listener: Option<WebEngineListenerMut>,
}

impl JniWebView {
    pub fn new(id: i64, webview_java_obj: GlobalRef) -> Self {
        JniWebView {
            webview_java_obj,
            id,
            // listener_id: 0,
            // listeners: Arc::new(RwLock::new(HashMap::new())),
            listener: None,
        }
    }
}

impl WebEngine for JniWebView {
    fn init(&mut self) -> Result<(), String> {
        let mut jni_handler = JniHandler::new();
        jni_handler.call_method(&self.webview_java_obj, METHOD_INIT, &[]);
        Ok(())
    }

    fn set_id(&mut self, id: i64) {
        self.id = id;
    }

    fn id(&self) -> i64 {
        self.id
    }

    fn load_url(&self, url: &str) -> Result<(), String> {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(&url);
        jni_handler.call_method(
            &self.webview_java_obj,
            METHOD_LOAD_URL,
            &[JValueGen::Object(&url_obj)],
        );
        Ok(())
    }

    fn load_data(&self, data: &str) -> Result<(), String> {
        let mut jni_handler = JniHandler::new();
        let data_obj = jni_handler.new_string(&data);
        jni_handler.call_method(
            &self.webview_java_obj,
            METHOD_LOAD_DATA,
            &[JValueGen::Object(&data_obj)],
        );
        Ok(())
    }

    fn reload(&self) -> Result<(), String> {
        let mut jni_handler = JniHandler::new();
        jni_handler.call_method(&self.webview_java_obj, METHOD_RELOAD, &[]);
        Ok(())
    }

    fn evaluate(&self, script: &str) -> Result<String, String> {
        let mut jni_handler = JniHandler::new();
        let script_obj = jni_handler.new_string(&script);
        let ret_obj = jni_handler.call_method(
            &self.webview_java_obj,
            METHOD_EVALUATE,
            &[JValueGen::Object(&script_obj)],
        );
        let ret = jni_handler.get_string(&ret_obj);
        Ok(ret)
    }

    // fn add_listener(&mut self, listener: WebEngineListenerMut) -> i64 {
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
    //     F: FnMut(WebEngineListenerMut),
    // {
    //     self.listeners
    //         .read()
    //         .unwrap()
    //         .values()
    //         .for_each(|l| callback(l.clone()));
    // }

    // fn listeners(&self) -> Vec<WebEngineListenerMut> {
    //     self.listeners
    //         .read()
    //         .unwrap()
    //         .values()
    //         .cloned()
    //         .collect::<Vec<_>>()
    // }

    fn set_listener(&mut self, listener: WebEngineListenerMut) {
        if self.listener.is_some() {
            return;
        }
        self.listener = Some(listener);
    }

    fn listener(&self) -> Option<WebEngineListenerMut> {
        self.listener.clone()
    }

    fn remove_listener(&mut self) {
        self.listener = None;
    }

    fn destroy(&mut self) -> Result<(), String> {
        let mut jni_handler = JniHandler::new();
        jni_handler.call_method(&self.webview_java_obj, METHOD_DESTROY, &[]);

        Ok(())
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageStarted<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) {
    let url: String = env.get_string(&url).expect("get url failed").into();

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    // let listeners = jni_wv.listeners();
    // listeners.iter().for_each(|listener| {
    //     listener.on_load_progress(jni_wv.clone(), 100);
    // });

    // jni_wv.notify_listeners(|listener| {
    //     listener.on_page_started(jni_wv.clone(), &url);
    // });
    jni_wv
        .read()
        .unwrap()
        .listener()
        .map(|l| l.write().unwrap().on_page_started(jni_wv.clone(), &url));
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageCancelled<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) {
    let url: String = env.get_string(&url).expect("get url failed").into();

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);
    jni_wv
        .read()
        .unwrap()
        .listener()
        .map(|l| l.write().unwrap().on_page_cancelled(jni_wv.clone(), &url));
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageFinished<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) {
    let url: String = env.get_string(&url).expect("get url failed").into();

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);
    jni_wv
        .read()
        .unwrap()
        .listener()
        .map(|l| l.write().unwrap().on_page_finished(jni_wv.clone(), &url));
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageError<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
    error: JString,
) {
    let url: String = env.get_string(&url).expect("get url failed").into();
    let error: String = env.get_string(&error).expect("get error failed").into();

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv.read().unwrap().listener().map(|l| {
        l.write()
            .unwrap()
            .on_page_error(jni_wv.clone(), &url, &error)
    });
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnLoadProgress<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    progress: jint,
) {
    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv.read().unwrap().listener().map(|l| {
        l.write()
            .unwrap()
            .on_load_progress(jni_wv.clone(), progress)
    });
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnShouldOverrideUrlLoading<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) -> jboolean {
    let url: String = env.get_string(&url).expect("get url failed").into();

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv
        .read()
        .unwrap()
        .listener()
        .map(|l| {
            l.write()
                .unwrap()
                .should_override_url_loading(jni_wv.clone(), &url)
        })
        .map(|b| b.into())
        .unwrap_or(JNI_FALSE)
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnShouldInterceptRequest<
    'local,
>(
    mut env: JNIEnv,
    this: JObject<'local>,
    url: JString,
) -> jstring {
    let url: String = env.get_string(&url).expect("get url failed").into();

    let jni_wv = get_jni_wv_from_java_obj(&mut env, this);

    jni_wv
        .read()
        .unwrap()
        .listener()
        .map(|l| {
            l.write()
                .unwrap()
                .should_intercept_request(jni_wv.clone(), &url)
        })
        .map(|s| {
            s.map(|s| {
                env.new_string(&s)
                    .expect(&format!("new string {} failed", &s))
            })
        })
        .map(|e| match e {
            Some(e) => e.into_raw(),
            None => JObject::null().into_raw(),
        })
        .unwrap_or(JObject::null().into_raw())
}

fn get_jni_wv_from_java_obj<'other_local, O>(env: &mut JNIEnv, obj: O) -> WebEngineMut
where
    O: AsRef<JObject<'other_local>>,
{
    let ptr = env.get_field(obj, JNI_WV_JAVA_FIELD_NAME_PTR, "J").unwrap();

    let wv_id = match ptr {
        JValueGen::Long(ptr) => Some(ptr),
        _ => None,
    }
    .expect("wv_id is none in java object");

    let wm = WebEngineManager::get_instance().lock().unwrap();

    wm.get_webengine(wv_id)
        .expect("webengine is none in web engine manager")
}
