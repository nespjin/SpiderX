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
    Env, EnvUnowned, jni_sig, jni_str,
    objects::{JClass, JObject, JString, JValue},
    refs::Global,
    signature::MethodSignature,
    strings::JNIStr,
    sys::{JNI_FALSE, jboolean, jint, jstring},
};
use once_cell::sync::OnceCell;

use crate::{
    jni::jni_utils::{self, JniFieldDetail, JniMethodDetail},
    web_engine::{
        web_engine::{WebEngine, WebEngineListenerArc, WebEngineMut},
        web_engine_manager::WebEngineManager,
    },
};

// const JAVA_CLASS_NAME_WV: &'static str = "com/nesp/spiderx/runtime/JniWebView";

pub const FIELD_PTR: JniFieldDetail = (jni_str!("mPtr"), jni_sig!(jlong));

const METHOD_INIT: JniMethodDetail = (
    jni_str!("init"),
    jni_sig!(() -> void), // ()V
);
const METHOD_LOAD_URL: JniMethodDetail = (
    jni_str!("loadUrl"),
    jni_sig!((java.lang.String) -> void), // (Ljava/lang/String;)V
);
const METHOD_LOAD_DATA: JniMethodDetail = (
    jni_str!("loadData"),
    jni_sig!((java.lang.String) -> void), // (Ljava/lang/String;)V
);
const METHOD_RELOAD: JniMethodDetail = (
    jni_str!("reload"),
    jni_sig!(() -> void), // ()V
);
const METHOD_EVALUATE: JniMethodDetail = (
    jni_str!("evaluate"),
    jni_sig!((java.lang.String) -> java.lang.String), // (Ljava/lang/String;)Ljava/lang/String;
);
const METHOD_DESTROY: JniMethodDetail = (
    jni_str!("destroy"),
    jni_sig!(() -> void), // ()V
);

pub const JNI_WV_JAVA_FIELD_NAME_PTR: &'static JNIStr = jni_str!("mPtr");

pub static JAVA_WEBVIEW_CLASS: OnceCell<Global<JClass<'static>>> = OnceCell::new();
const JAVA_WV_CTOR_SIG: MethodSignature = jni_sig!(() -> void);

pub fn get_java_webview_class() -> Result<&'static Global<JClass<'static>>, String> {
    JAVA_WEBVIEW_CLASS
        .get()
        .ok_or_else(|| "Java WebView class not initialized".to_string())
}

pub fn new_webview_obj() -> Result<Global<JObject<'static>>, jni::errors::Error> {
    jni_utils::attach_current_thread(
        |env| -> Result<Global<JObject<'static>>, jni::errors::Error> {
            let clazz =
                get_java_webview_class().map_err(|_| jni::errors::Error::ClassNotFound {
                    name: "JniWebView".into(),
                })?;
            let obj = env.new_object(clazz, JAVA_WV_CTOR_SIG, &[])?;
            env.new_global_ref(obj)
        },
    )
}
pub fn new_jni_webview(id: i64) -> Result<JniWebView, jni::errors::Error> {
    jni_utils::attach_current_thread(|env| -> Result<JniWebView, jni::errors::Error> {
        let webview_obj = env.new_global_ref(new_webview_obj()?)?;
        env.set_field(&webview_obj, FIELD_PTR.0, FIELD_PTR.1, JValue::Long(id))?;
        Ok(JniWebView::new(id, webview_obj))
    })
}

pub struct JniWebView {
    webview_java_obj: Global<JObject<'static>>,
    id: i64,
    // listener_id: i64,
    // listeners: Arc<RwLock<HashMap<i64, WebEngineListenerArc>>>,
    listener: Option<WebEngineListenerArc>,
}

impl JniWebView {
    pub fn new(id: i64, webview_java_obj: Global<JObject<'static>>) -> Self {
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
        log::debug!("init webview: {}", self.id);
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            env.call_method(&self.webview_java_obj, METHOD_INIT.0, METHOD_INIT.1, &[])?;
            Ok(())
        })
        .map_err(|e| e.to_string())
    }

    fn set_id(&mut self, id: i64) {
        self.id = id;
    }

    fn id(&self) -> i64 {
        self.id
    }

    fn load_url(&self, url: &str) -> Result<(), String> {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(&url)?;
            env.call_method(
                &self.webview_java_obj,
                METHOD_LOAD_URL.0,
                METHOD_LOAD_URL.1,
                &[JValue::Object(&url_obj)],
            )?;
            env.delete_local_ref(url_obj);
            Ok(())
        })
        .map_err(|e| e.to_string())
    }

    fn load_data(&self, data: &str) -> Result<(), String> {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let data_obj = env.new_string(&data)?;
            env.call_method(
                &self.webview_java_obj,
                METHOD_LOAD_DATA.0,
                METHOD_LOAD_DATA.1,
                &[JValue::Object(&data_obj)],
            )?;
            env.delete_local_ref(data_obj);
            Ok(())
        })
        .map_err(|e| e.to_string())
    }

    fn reload(&self) -> Result<(), String> {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            env.call_method(
                &self.webview_java_obj,
                METHOD_RELOAD.0,
                METHOD_RELOAD.1,
                &[],
            )?;
            Ok(())
        })
        .map_err(|e| e.to_string())
    }

    fn evaluate(&self, script: &str) -> Result<String, String> {
        jni_utils::attach_current_thread(|env| -> Result<String, jni::errors::Error> {
            let script_obj = env.new_string(&script)?;
            let ret_obj = env
                .call_method(
                    &self.webview_java_obj,
                    METHOD_EVALUATE.0,
                    METHOD_EVALUATE.1,
                    &[JValue::Object(&script_obj)],
                )?
                .into_object()?;
            env.delete_local_ref(script_obj);
            if ret_obj.is_null() {
                return Ok("".to_string());
            }
            let ret_str = JString::cast_local(env, ret_obj)?;
            let ret = ret_str.to_string();
            env.delete_local_ref(ret_str);
            Ok(ret)
        })
        .map_err(|e| e.to_string())
    }

    fn set_listener(&mut self, listener: WebEngineListenerArc) {
        if self.listener.is_some() {
            return;
        }
        self.listener = Some(listener);
    }

    fn listener(&self) -> Option<WebEngineListenerArc> {
        self.listener.clone()
    }

    fn remove_listener(&mut self) {
        self.listener = None;
    }

    fn destroy(&mut self) -> Result<(), String> {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            env.call_method(
                &self.webview_java_obj,
                METHOD_DESTROY.0,
                METHOD_DESTROY.1,
                &[],
            )?;
            Ok(())
        })
        .map_err(|e| e.to_string())
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageStarted<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
) {
    let url: String = url.to_string();

    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);

            jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.on_page_started(jni_wv.clone(), &url));

            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageCancelled<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
) {
    let url: String = url.to_string();

    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.on_page_cancelled(jni_wv.clone(), &url));

            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageFinished<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
    document: JString,
) {
    let url: String = url.to_string();

    log::debug!("on page finished 0: {}", url);

    let document = document.to_string();

    log::debug!("on page finished 1: {}", url);

    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.on_page_finished(jni_wv.clone(), &url, &document));

            log::debug!("on page finished end: {}", url);
            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnPageError<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
    error: JString,
) {
    let url: String = url.to_string();
    let error: String = error.to_string();

    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.on_page_error(jni_wv.clone(), &url, &error));
            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnLoadProgress<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
    progress: jint,
) {
    let url: String = url.to_string();

    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.on_load_progress(jni_wv.clone(), &url, progress));
            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnShouldOverrideUrlLoading<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
) -> jboolean {
    let url: String = url.to_string();

    unowned_env
        .with_env(|env| -> Result<jboolean, jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            let ret = jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.should_override_url_loading(jni_wv.clone(), &url))
                .unwrap_or(JNI_FALSE);

            Ok(ret)
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnShouldInterceptRequest<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
) -> jstring {
    let url: String = url.to_string();

    unowned_env
        .with_env(|env| -> Result<jstring, jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            let ret = jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.should_intercept_request(jni_wv.clone(), &url))
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
                .unwrap_or(JObject::null().into_raw());

            Ok(ret)
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnReceivedData<
    'local,
>(
    mut unowned_env: EnvUnowned,
    this: JObject<'local>,
    url: JString,
    data: JString,
) {
    let url: String = url.to_string();
    let data: String = data.to_string();

    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.on_received_data(jni_wv.clone(), &url, &data));
            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_JniWebView_nativeNotifyOnReceivedError<
    'local,
>(
    mut unowned_env: EnvUnowned<'local>,
    this: JObject<'local>,
    url: JString,
    error: JString,
) {
    let url: String = url.to_string();
    let error: String = error.to_string();

    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let jni_wv = get_jni_wv_from_java_obj(env, this);
            jni_wv
                .read()
                .unwrap()
                .listener()
                .map(|l| l.on_received_error(jni_wv.clone(), &url, &error));
            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

fn get_jni_wv_from_java_obj<'other_local, O>(env: &mut Env, obj: O) -> WebEngineMut
where
    O: AsRef<JObject<'other_local>>,
{
    let ptr = env
        .get_field(obj, JNI_WV_JAVA_FIELD_NAME_PTR, jni_sig!(jlong))
        .expect("get ptr failed")
        .into_long()
        .expect("wv_id is none in java object");

    let wv_id = ptr;

    let wm = WebEngineManager::get_instance();
    wm.get_webengine(wv_id)
        .expect("webengine is none in web engine manager")
}
