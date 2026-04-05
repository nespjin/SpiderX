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
use jni::sys::JNI_FALSE;
use jni::sys::JNI_TRUE;
use jni::sys::jboolean;
use jni::sys::jint;
use jni::sys::jobject;
use jni::sys::jstring;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::OnceLock;

use crate::device::device_manager::DeviceManager;
use crate::executor::request_dataset_listener::RequestDatasetListener;
use crate::executor::request_dataset_listener::RequestDatasetListenerWrpper;
use crate::executor::request_dataset_listener::RequestJavaScriptDatasetListener;
use crate::jni::jni_classes;
use crate::jni::jni_handler::JniHandler;
use crate::jni::jni_handler::JniMethodInfo;
use crate::jni::jni_methods;
use crate::jni::jni_plugin::JniPlugin;
use crate::jni::jni_screen_type::JniScreenType;
use crate::jni::jni_webview::JAVA_WEBVIEW_CLASS;
use crate::plugin_manager::PluginManager;
use crate::plugin_manager::PluginManagerConfig;
use crate::plugin_manager::PluginSource;
use crate::plugin_manager::RequestType;
use crate::utils::log_utils;
use crate::web_engine::web_engine_manager::WebEngineManager;

pub const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON: jint = 0;
pub const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE: jint = 1;

pub struct JniPluginManager {
    is_initialized: bool,
}

impl JniPluginManager {
    pub fn get_instance() -> &'static Mutex<JniPluginManager> {
        static INSTANCE: OnceLock<Mutex<JniPluginManager>> = OnceLock::new();
        INSTANCE.get_or_init(|| {
            Mutex::new(JniPluginManager {
                is_initialized: false,
            })
        })
    }

    pub fn init(&mut self, wv_java_class: GlobalRef) -> Result<(), String> {
        if self.is_initialized {
            return Err("The jni plugin manager is already init.".to_string());
        }

        self.is_initialized = true;

        if let Err(_) = JAVA_WEBVIEW_CLASS.set(wv_java_class) {
            return Err("Failed to set global Java WebView class.".to_string());
        }
        Ok(())
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInit(
    env: JNIEnv,
    _this: JObject,
    databasePath: JString,
    screenType: JObject,
    isCacheEngine: jboolean,
    webviewClass: JClass<'static>,
) {
    let mut handler = JniHandler::new_with_env(env);
    let database_path = handler.get_string(&databasePath);

    {
        let config = PluginManagerConfig { database_path };
        let mut plugin_manager = PluginManager::get_instance().lock().unwrap();
        handler.throw_java_expception_if_error(plugin_manager.init(config));
    }

    {
        let mut jni_plugin_manager = JniPluginManager::get_instance().lock().unwrap();
        let wv_java_class_global = handler.new_global_ref(&webviewClass);
        handler.throw_java_expception_if_error(jni_plugin_manager.init(wv_java_class_global));
    }

    {
        let mut wm = WebEngineManager::get_instance().lock().unwrap();
        wm.init(isCacheEngine == JNI_TRUE).unwrap();
    }

    {
        let mut jni_screen_type = JniScreenType::new(&mut handler);
        let screen_type = jni_screen_type.java_object_to_screen_type(screenType);
        let mut dm = DeviceManager::get_instance().lock().unwrap();
        dm.set_screen_type(screen_type);
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeSetScreenType(
    env: JNIEnv,
    _this: JObject,
    screenType: JObject,
) {
    let mut handler = JniHandler::new_with_env(env);

    let mut jni_screen_type = JniScreenType::new(&mut handler);
    let screen_type = jni_screen_type.java_object_to_screen_type(screenType);

    let mut dm = DeviceManager::get_instance().lock().unwrap();
    dm.set_screen_type(screen_type);
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInstallPlugin(
    env: JNIEnv,
    _this: JObject,
    sourceType: jint,
    source: JByteArray,
) {
    let mut handler = JniHandler::new_with_env(env);

    let array_len = handler.get_array_length(&source);

    let mut buf = vec![0; array_len as usize];
    handler.get_byte_array_region(&source, 0, &mut buf);
    let buf: Vec<u8> = unsafe { std::mem::transmute(buf) };

    let plugin_source = match sourceType {
        JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON | JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE => {
            let source = String::from_utf8(buf).expect("from utf8 failed");
            match sourceType {
                JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON => PluginSource::ManifestJson(source),
                JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE | _ => {
                    PluginSource::ManifestJsonFile(source)
                }
            }
        }
        _ => {
            handler.throw_java_expception_msg(&format!("Invalid source type {}.", sourceType));
            return;
        }
    };

    let plugin_manager = PluginManager::get_instance().lock().unwrap();
    handler.throw_java_expception_if_error(plugin_manager.install_plugin(&plugin_source));
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeIsPluginInstalled(
    env: JNIEnv,
    _this: JObject,
    id: JString,
) -> jboolean {
    let mut handler = JniHandler::new_with_env(env);
    let id = handler.get_string(&id);

    let is_installed = {
        let plugin_manager = PluginManager::get_instance().lock().unwrap();
        plugin_manager.is_plugin_installed(&id)
    };

    handler
        .throw_java_expception_if_error(is_installed)
        .map(|e| e.into())
        .unwrap_or(JNI_FALSE)
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeGetInstalledPlugin(
    env: JNIEnv,
    _this: JObject,
    id: JString,
) -> jobject {
    let mut handler = JniHandler::new_with_env(env);
    let id = handler.get_string(&id);

    let installed_plugin = {
        let plugin_manager = PluginManager::get_instance().lock().unwrap();
        plugin_manager.get_installed_plugin(&id)
    };

    handler
        .throw_java_expception_if_error(installed_plugin)
        .flatten()
        .map(|e| JniPlugin::clone_from_plugin(&mut handler, &e))
        .map(|e| e.unsafe_jobject())
        .map(|e| e.into_raw())
        .unwrap_or(JObject::null().into_raw())
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeGetInstalledPlugins(
    env: JNIEnv,
    _this: JObject,
) -> jobject {
    let mut handler = JniHandler::new_with_env(env);

    let plugins = {
        let plugin_manager = PluginManager::get_instance().lock().unwrap();
        match handler.throw_java_expception_if_error(plugin_manager.get_installed_plugins()) {
            Some(p) => p,
            None => return JObject::null().into_raw(),
        }
    };

    let arr_list = handler.new_object(jni_classes::ARRAY_LIST_CONSTOR, &[]);

    for plugin in plugins {
        let jni_plugin = JniPlugin::clone_from_plugin(&mut handler, &plugin);
        let plugin_obj = jni_plugin.unsafe_jobject();
        handler.call_method(
            &arr_list,
            jni_methods::LIST_ADD,
            &[JValueGen::Object(&plugin_obj)],
        );
    }

    arr_list.into_raw()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeUninstallPlugin(
    env: JNIEnv,
    _this: JObject,
    id: JString,
) {
    let mut handler = JniHandler::new_with_env(env);
    let id = handler.get_string(&id);

    let mut plugin_manager = PluginManager::get_instance().lock().unwrap();
    handler.throw_java_expception_if_error(plugin_manager.uninstall_plugin(&id));
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeRequestDataset(
    env: JNIEnv,
    _this: JObject,
    pluginId: JString,
    datasetId: JString,
    r#type: jint,
    listener: JObject,
) -> jstring {
    let mut handler = JniHandler::new_with_env(env);
    let plugin_id = handler.get_string(&pluginId);
    let dataset_id = handler.get_string(&datasetId);

    let data = {
        let plugin_manager = PluginManager::get_instance().lock().unwrap();
        let mut request_type = RequestType::try_from(r#type).unwrap();
        request_type = match request_type {
            RequestType::Auto => plugin_manager
                .auto_request_type(&plugin_id, &dataset_id)
                .unwrap(),
            _ => request_type,
        };

        let jni_listener = handler.new_global_ref(listener);

        let listener: Option<RequestDatasetListenerWrpper> = if jni_listener.is_null() {
            None
        } else {
            match request_type {
                RequestType::Auto => None,
                RequestType::JavaScript => {
                    let data = JniRequestJavaScriptDatasetListener::new(jni_listener);
                    let arc = Arc::new(data);
                    let java_script_dataset = RequestDatasetListenerWrpper::JavaScriptDataset(arc);
                    Some(java_script_dataset)
                }
                RequestType::Dsl => {
                    let data = JniRequestDatasetListener::new(jni_listener);
                    Some(RequestDatasetListenerWrpper::Dataset(Arc::new(data)))
                }
            }
        };

        plugin_manager.request_dataset(&plugin_id, &dataset_id, request_type, listener)
    };

    log_utils::logd(&format!(
        "Java_com_nesp_spiderx_runtime_PluginManager_nativeRequestDataset {} {} {:?}",
        plugin_id, dataset_id, data
    ));

    // handler
    //     .throw_java_expception_if_error(data)
    data.map(|e| handler.new_string(&e))
        .map(|e| e.into_raw())
        .unwrap_or(JObject::null().into_raw())
}

struct JniRequestDatasetListener {
    jni_listener: GlobalRef,
}

impl JniRequestDatasetListener {
    fn new(jni_listener: GlobalRef) -> Self {
        Self {
            jni_listener: jni_listener,
        }
    }
}

const REQUEST_DATASET_LISTENER_ON_RECEIVED_DATA: JniMethodInfo =
    ("onReceivedData", "(Ljava/lang/String;Ljava/lang/String;)V");
const REQUEST_DATASET_LISTENER_ON_RECEIVED_ERROR: JniMethodInfo =
    ("onReceivedError", "(Ljava/lang/String;Ljava/lang/String;)V");
const REQUEST_DATASET_LISTENER_ON_PAGE_STARTED: JniMethodInfo =
    ("onPageStarted", "(Ljava/lang/String;)V");
const REQUEST_DATASET_LISTENER_ON_PAGE_CANCELLED: JniMethodInfo =
    ("onPageCancelled", "(Ljava/lang/String;)V");
const REQUEST_DATASET_LISTENER_ON_PAGE_FINISHED: JniMethodInfo =
    ("onPageFinished", "(Ljava/lang/String;Ljava/lang/String;)V");
const REQUEST_DATASET_LISTENER_ON_PAGE_ERROR: JniMethodInfo =
    ("onPageError", "(Ljava/lang/String;Ljava/lang/String;)V");
const REQUEST_DATASET_LISTENER_ON_LOAD_PROGRESS: JniMethodInfo =
    ("onLoadProgress", "(Ljava/lang/String;I)V");
const REQUEST_DATASET_LISTENER_ON_SHOULD_OVERRIDE_URL_LOADING: JniMethodInfo =
    ("onShouldOverrideUrlLoading", "(Ljava/lang/String;)V");
const REQUEST_DATASET_LISTENER_ON_SHOULD_INTERCEPT_REQUEST: JniMethodInfo =
    ("onShouldInterceptRequest", "(Ljava/lang/String;)V");

fn handle_on_receive_data(jni_listener: &GlobalRef, url: &str, data: &str) {
    let mut jni_handler = JniHandler::new();
    let url_obj = jni_handler.new_string(url);
    let data_obj = jni_handler.new_string(data);

    jni_handler.call_method(
        jni_listener,
        REQUEST_DATASET_LISTENER_ON_RECEIVED_DATA,
        &[JValueGen::Object(&url_obj), JValueGen::Object(&data_obj)],
    );

    jni_handler.delete_local_ref(url_obj);
    jni_handler.delete_local_ref(data_obj);
}

fn handle_on_receive_error(jni_listener: &GlobalRef, url: &str, error: &str) {
    let mut jni_handler = JniHandler::new();
    let url_obj = jni_handler.new_string(url);
    let error_obj = jni_handler.new_string(error);

    jni_handler.call_method(
        jni_listener,
        REQUEST_DATASET_LISTENER_ON_RECEIVED_ERROR,
        &[JValueGen::Object(&url_obj), JValueGen::Object(&error_obj)],
    );

    jni_handler.delete_local_ref(url_obj);
    jni_handler.delete_local_ref(error_obj);
}

impl RequestDatasetListener for JniRequestDatasetListener {
    fn on_receive_data(&self, url: &str, data: &str) {
        handle_on_receive_data(&self.jni_listener, url, data);
    }

    fn on_receive_error(&self, url: &str, error: &str) {
        handle_on_receive_error(&self.jni_listener, url, error);
    }
}

struct JniRequestJavaScriptDatasetListener {
    jniListener: GlobalRef,
}

impl JniRequestJavaScriptDatasetListener {
    fn new(jni_listener: GlobalRef) -> Self {
        Self {
            jniListener: jni_listener,
        }
    }
}

impl RequestDatasetListener for JniRequestJavaScriptDatasetListener {
    fn on_receive_data(&self, url: &str, data: &str) {
        handle_on_receive_data(&self.jniListener, url, data);
    }

    fn on_receive_error(&self, url: &str, error: &str) {
        handle_on_receive_error(&self.jniListener, url, error);
    }
}

impl RequestJavaScriptDatasetListener for JniRequestJavaScriptDatasetListener {
    fn on_page_started(&self, url: &str) {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(url);

        jni_handler.call_method(
            &self.jniListener,
            REQUEST_DATASET_LISTENER_ON_PAGE_STARTED,
            &[JValueGen::Object(&url_obj)],
        );

        jni_handler.delete_local_ref(url_obj);
    }

    fn on_page_cancelled(&self, url: &str) {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(url);

        jni_handler.call_method(
            &self.jniListener,
            REQUEST_DATASET_LISTENER_ON_PAGE_CANCELLED,
            &[JValueGen::Object(&url_obj)],
        );

        jni_handler.delete_local_ref(url_obj);
    }

    fn on_page_finished(&self, url: &str, document: &str) {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(url);
        let document_obj = jni_handler.new_string(document);

        jni_handler.call_method(
            &self.jniListener,
            REQUEST_DATASET_LISTENER_ON_PAGE_FINISHED,
            &[
                JValueGen::Object(&url_obj),
                JValueGen::Object(&document_obj),
            ],
        );

        jni_handler.delete_local_ref(url_obj);
        jni_handler.delete_local_ref(document_obj);
    }

    fn on_page_error(&self, url: &str, error: &str) {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(url);
        let error_obj = jni_handler.new_string(error);

        jni_handler.call_method(
            &self.jniListener,
            REQUEST_DATASET_LISTENER_ON_PAGE_ERROR,
            &[JValueGen::Object(&url_obj), JValueGen::Object(&error_obj)],
        );

        jni_handler.delete_local_ref(url_obj);
        jni_handler.delete_local_ref(error_obj);
    }

    fn on_load_progress(&self, url: &str, progress: i32) {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(url);

        jni_handler.call_method(
            &self.jniListener,
            REQUEST_DATASET_LISTENER_ON_LOAD_PROGRESS,
            &[JValueGen::Object(&url_obj), JValueGen::Int(progress)],
        );

        jni_handler.delete_local_ref(url_obj);
    }

    fn on_should_override_url_loading(&self, url: &str) {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(url);

        jni_handler.call_method(
            &self.jniListener,
            REQUEST_DATASET_LISTENER_ON_SHOULD_OVERRIDE_URL_LOADING,
            &[JValueGen::Object(&url_obj)],
        );

        jni_handler.delete_local_ref(url_obj);
    }

    fn on_should_intercept_request(&self, url: &str) {
        let mut jni_handler = JniHandler::new();
        let url_obj = jni_handler.new_string(url);

        jni_handler.call_method(
            &self.jniListener,
            REQUEST_DATASET_LISTENER_ON_SHOULD_INTERCEPT_REQUEST,
            &[JValueGen::Object(&url_obj)],
        );

        jni_handler.delete_local_ref(url_obj);
    }
}
