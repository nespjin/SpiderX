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

use jni::EnvUnowned;
use jni::jni_sig;
use jni::jni_str;
use jni::objects::*;
use jni::sys::JNI_FALSE;
use jni::sys::JNI_TRUE;
use jni::sys::jboolean;
use jni::sys::jint;
use jni::sys::jobject;
use jni::sys::jshort;
use jni::sys::jstring;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::OnceLock;

use crate::device::device_manager::DeviceManager;
use crate::executor::request_dataset_listener::RequestDatasetListener;
use crate::executor::request_dataset_listener::RequestDatasetListenerWrpper;
use crate::executor::request_dataset_listener::RequestJavaScriptDatasetListener;
use crate::jni::jni_plugin::JniPlugin;
use crate::jni::jni_screen_type::JniScreenType;
use crate::jni::jni_utils;
use crate::jni::jni_utils::JniMethodDetail;
use crate::jni::jni_webview::JAVA_WEBVIEW_CLASS;
use crate::plugin_manager::PluginManager;
use crate::plugin_manager::PluginManagerConfig;
use crate::plugin_manager::PluginSource;
use crate::plugin_manager::RequestType;
use crate::repository::request_dataset_options::RequestDatasetOptions;
use crate::web_engine::web_engine_manager::WebEngineManager;

pub const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON: jint = 0;
pub const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE: jint = 1;

pub struct JniPluginManager {
    is_initialized: Mutex<bool>,
}

impl JniPluginManager {
    pub fn get_instance() -> &'static JniPluginManager {
        static INSTANCE: OnceLock<JniPluginManager> = OnceLock::new();
        INSTANCE.get_or_init(|| JniPluginManager {
            is_initialized: Mutex::new(false),
        })
    }

    pub fn init(&self, wv_java_class: Global<JClass<'static>>) -> Result<(), String> {
        {
            let mut is_initialized = self.is_initialized.lock().unwrap();
            if *is_initialized {
                return Err("The jni plugin manager is already init.".to_string());
            }
            *is_initialized = true;
        }

        JAVA_WEBVIEW_CLASS
            .set(wv_java_class)
            .map(|_| ())
            .map_err(|_| "Failed to set global Java WebView class.".to_string())
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInit<'local>(
    mut unowned_env: EnvUnowned<'local>,
    _this: JObject,
    databasePath: JString<'local>,
    screenType: JObject,
    isCacheEngine: jboolean,
    webviewClass: JClass<'static>,
) {
    unowned_env
        .with_env(|env| -> Result<(), jni::errors::Error> {
            let database_path = databasePath.to_string();

            {
                let config = PluginManagerConfig { database_path };
                let plugin_manager = PluginManager::get_instance();
                jni_utils::throw_java_exception_if_error(env, plugin_manager.init(config));
            }

            {
                let jni_plugin_manager = JniPluginManager::get_instance();
                let wv_java_class_global = env.new_global_ref(&webviewClass)?;
                jni_utils::throw_java_exception_if_error(
                    env,
                    jni_plugin_manager.init(wv_java_class_global),
                );
            }

            {
                let wm = WebEngineManager::get_instance();
                jni_utils::throw_java_exception_if_error(env, wm.init(isCacheEngine == JNI_TRUE));
            }

            {
                let mut jni_screen_type = JniScreenType::new();
                let screen_type = jni_screen_type.java_object_to_screen_type(env, screenType)?;
                let dm = DeviceManager::get_instance();
                dm.set_screen_type(screen_type);
            }
            Ok(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>();
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeSetScreenType(
    mut unowned_env: EnvUnowned,
    _this: JObject,
    screenType: JObject,
) {
    unowned_env
        .with_env(|env| {
            let mut jni_screen_type = JniScreenType::new();
            let screen_type = jni_screen_type.java_object_to_screen_type(env, screenType)?;

            let dm = DeviceManager::get_instance();
            dm.set_screen_type(screen_type);

            Ok::<(), jni::errors::Error>(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>();
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInstallPlugin(
    mut unowned_env: EnvUnowned,
    _this: JObject,
    sourceType: jint,
    source: JByteArray,
) {
    unowned_env
        .with_env(|env| {
            let array_len = source.len(env)?;

            let mut buf = vec![0; array_len];
            source.get_region(env, 0, &mut buf)?;
            let buf: Vec<u8> = unsafe { std::mem::transmute(buf) };

            let plugin_source = match sourceType {
                JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON
                | JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE => {
                    let source = String::from_utf8(buf).expect("from utf8 failed");
                    match sourceType {
                        JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON => PluginSource::ManifestJson(source),
                        JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE | _ => {
                            PluginSource::ManifestJsonFile(source)
                        }
                    }
                }
                _ => {
                    jni_utils::throw_java_exception_msg(
                        env,
                        &format!("Invalid source type {}.", sourceType),
                    );
                    return Err(jni::errors::Error::IllegalMonitorState);
                }
            };

            let plugin_manager = PluginManager::get_instance();
            jni_utils::throw_java_exception_if_error(
                env,
                plugin_manager.install_plugin(&plugin_source),
            );

            Ok::<(), jni::errors::Error>(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>();
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeIsPluginInstalled(
    mut unowned_env: EnvUnowned,
    _this: JObject,
    id: JString,
) -> jboolean {
    unowned_env
        .with_env(|env| -> Result<jboolean, jni::errors::Error> {
            let id = id.to_string();

            let is_installed = {
                let plugin_manager = PluginManager::get_instance();
                plugin_manager.is_plugin_installed(&id)
            };

            let ret =
                jni_utils::throw_java_exception_if_error(env, is_installed).unwrap_or(JNI_FALSE);
            Ok(ret)
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeGetInstalledPlugin(
    mut unowned_env: EnvUnowned,
    _this: JObject,
    id: JString,
) -> jobject {
    let id = id.to_string();

    let installed_plugin = {
        let plugin_manager = PluginManager::get_instance();
        plugin_manager.get_installed_plugin(&id)
    };

    unowned_env
        .with_env(|env| -> Result<jobject, jni::errors::Error> {
            let ret = jni_utils::throw_java_exception_if_error(env, installed_plugin)
                .flatten()
                .map(|e| JniPlugin::clone_from_plugin(env, &e))
                .map(|e| e.into_raw_jobject())
                .unwrap_or(JObject::null().into_raw());

            Ok(ret)
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeGetInstalledPlugins(
    mut unowned_env: EnvUnowned,
    _this: JObject,
) -> jobject {
    unowned_env
        .with_env(|env| -> Result<jobject, jni::errors::Error> {
            let plugins = {
                let plugin_manager = PluginManager::get_instance();
                match jni_utils::throw_java_exception_if_error(
                    env,
                    plugin_manager.get_installed_plugins(),
                ) {
                    Some(p) => p,
                    None => return Ok(JObject::null().into_raw()),
                }
            };

            let arr_list = env.new_object(
                jni_utils::ARRAY_LIST_CONSTOR.0,
                jni_utils::ARRAY_LIST_CONSTOR.1,
                &[],
            )?;

            for plugin in plugins {
                let jni_plugin = JniPlugin::clone_from_plugin(env, &plugin);
                let plugin_obj = jni_plugin.jobject_ref();
                env.call_method(
                    &arr_list,
                    jni_utils::LIST_ADD.0,
                    jni_utils::LIST_ADD.1,
                    &[JValue::Object(plugin_obj)],
                )?;
            }

            Ok(arr_list.into_raw())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeUninstallPlugin(
    mut unowned_env: EnvUnowned,
    _this: JObject,
    id: JString,
) {
    let plugin_manager = PluginManager::get_instance();
    unowned_env
        .with_env(|env| {
            jni_utils::throw_java_exception_if_error(
                env,
                plugin_manager.uninstall_plugin(&id.to_string()),
            );
            Ok::<(), jni::errors::Error>(())
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>();
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeRequestDataset(
    mut unowned_env: EnvUnowned,
    _this: JObject,
    pluginId: JString,
    datasetId: JString,
    timeout: jshort,
    r#type: jint,
    listener: JObject,
) -> jstring {
    unowned_env
        .with_env(|env| -> Result<jstring, jni::errors::Error> {
            let plugin_id = pluginId.to_string();
            let dataset_id = datasetId.to_string();

            let data = {
                let plugin_manager = PluginManager::get_instance();
                let mut request_type = RequestType::try_from(r#type).unwrap();
                request_type = match request_type {
                    RequestType::Auto => plugin_manager
                        .auto_request_type(&plugin_id, &dataset_id)
                        .unwrap(),
                    _ => request_type,
                };

                let jni_listener = env.new_global_ref(listener)?;

                let listener: Option<RequestDatasetListenerWrpper> = if jni_listener.is_null() {
                    None
                } else {
                    match request_type {
                        RequestType::Auto => None,
                        RequestType::JavaScript => {
                            let data = JniRequestJavaScriptDatasetListener::new(jni_listener);
                            let arc = Arc::new(data);
                            let java_script_dataset =
                                RequestDatasetListenerWrpper::JavaScriptDataset(arc);
                            Some(java_script_dataset)
                        }
                        RequestType::Dsl => {
                            let data = JniRequestDatasetListener::new(jni_listener);
                            Some(RequestDatasetListenerWrpper::Dataset(Arc::new(data)))
                        }
                    }
                };

                let mut options = RequestDatasetOptions::new();
                options
                    .with_timeout(timeout as u16)
                    .with_type(request_type)
                    .with_opt_listener(listener);
                plugin_manager.request_dataset(&plugin_id, &dataset_id, options)
            };

            log::debug!(
                "Java_com_nesp_spiderx_runtime_PluginManager_nativeRequestDataset {} {} {:?}",
                plugin_id,
                dataset_id,
                data
            );

            let ret = jni_utils::throw_java_exception_if_error(env, data)
                .map(|e| JString::new(env, e))
                .map(|e| e.map(|e| e.into_raw()))
                .map(|e| e.ok())
                .flatten()
                .unwrap_or(JObject::null().into_raw());

            Ok(ret)
        })
        .resolve::<jni::errors::ThrowRuntimeExAndDefault>()
}

struct JniRequestDatasetListener {
    jni_listener: Global<JObject<'static>>,
}

impl JniRequestDatasetListener {
    fn new(jni_listener: Global<JObject<'static>>) -> Self {
        Self {
            jni_listener: jni_listener,
        }
    }
}

const REQUEST_DATASET_LISTENER_ON_RECEIVED_DATA: JniMethodDetail = (
    jni_str!("onReceivedData"),
    jni_sig!((java.lang.String, java.lang.String) -> void), // (Ljava/lang/String;Ljava/lang/String;)V
);
const REQUEST_DATASET_LISTENER_ON_RECEIVED_ERROR: JniMethodDetail = (
    jni_str!("onReceivedError"),
    jni_sig!((java.lang.String, java.lang.String) -> void), // (Ljava/lang/String;Ljava/lang/String;)V
);
const REQUEST_DATASET_LISTENER_ON_PAGE_STARTED: JniMethodDetail = (
    jni_str!("onPageStarted"),
    jni_sig!((java.lang.String) -> void), // (Ljava/lang/String;)V
);
const REQUEST_DATASET_LISTENER_ON_PAGE_CANCELLED: JniMethodDetail = (
    jni_str!("onPageCancelled"),
    jni_sig!((java.lang.String) -> void), // (Ljava/lang/String;)V
);
const REQUEST_DATASET_LISTENER_ON_PAGE_FINISHED: JniMethodDetail = (
    jni_str!("onPageFinished"),
    jni_sig!((java.lang.String, java.lang.String) -> void), // (Ljava/lang/String;Ljava/lang/String;)V
);
const REQUEST_DATASET_LISTENER_ON_PAGE_ERROR: JniMethodDetail = (
    jni_str!("onPageError"),
    jni_sig!((java.lang.String, java.lang.String) -> void), // (Ljava/lang/String;Ljava/lang/String;)V
);
const REQUEST_DATASET_LISTENER_ON_LOAD_PROGRESS: JniMethodDetail = (
    jni_str!("onLoadProgress"),
    jni_sig!((java.lang.String, jint) -> void), // (Ljava/lang/String;I)V
);
const REQUEST_DATASET_LISTENER_ON_SHOULD_OVERRIDE_URL_LOADING: JniMethodDetail = (
    jni_str!("onShouldOverrideUrlLoading"),
    jni_sig!((java.lang.String) -> void), // (Ljava/lang/String;)V
);
const REQUEST_DATASET_LISTENER_ON_SHOULD_INTERCEPT_REQUEST: JniMethodDetail = (
    jni_str!("onShouldInterceptRequest"),
    jni_sig!((java.lang.String) -> void), // (Ljava/lang/String;)V
);

fn handle_on_receive_data(jni_listener: &Global<JObject<'static>>, url: &str, data: &str) {
    jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
        let url_obj = env.new_string(url)?;
        let data_obj = env.new_string(data)?;

        env.call_method(
            jni_listener,
            REQUEST_DATASET_LISTENER_ON_RECEIVED_DATA.0,
            REQUEST_DATASET_LISTENER_ON_RECEIVED_DATA.1,
            &[JValue::Object(&url_obj), JValue::Object(&data_obj)],
        )?;

        env.delete_local_ref(url_obj);
        env.delete_local_ref(data_obj);

        Ok(())
    })
    .expect("Failed to handle receive data");
}

fn handle_on_receive_error(jni_listener: &Global<JObject<'static>>, url: &str, error: &str) {
    jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
        let url_obj = env.new_string(url)?;
        let error_obj = env.new_string(error)?;

        env.call_method(
            jni_listener,
            REQUEST_DATASET_LISTENER_ON_RECEIVED_ERROR.0,
            REQUEST_DATASET_LISTENER_ON_RECEIVED_ERROR.1,
            &[JValue::Object(&url_obj), JValue::Object(&error_obj)],
        )?;

        env.delete_local_ref(url_obj);
        env.delete_local_ref(error_obj);

        Ok(())
    })
    .expect("Failed to handle receive error");
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
    jni_listener: Global<JObject<'static>>,
}

impl JniRequestJavaScriptDatasetListener {
    fn new(jni_listener: Global<JObject<'static>>) -> Self {
        Self { jni_listener }
    }
}

impl RequestDatasetListener for JniRequestJavaScriptDatasetListener {
    fn on_receive_data(&self, url: &str, data: &str) {
        handle_on_receive_data(&self.jni_listener, url, data);
    }

    fn on_receive_error(&self, url: &str, error: &str) {
        handle_on_receive_error(&self.jni_listener, url, error);
    }
}

impl RequestJavaScriptDatasetListener for JniRequestJavaScriptDatasetListener {
    fn on_page_started(&self, url: &str) {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(url)?;

            env.call_method(
                &self.jni_listener,
                REQUEST_DATASET_LISTENER_ON_PAGE_STARTED.0,
                REQUEST_DATASET_LISTENER_ON_PAGE_STARTED.1,
                &[JValue::Object(&url_obj)],
            )?;

            env.delete_local_ref(url_obj);

            Ok(())
        })
        .expect("Error in on_page_started");
    }

    fn on_page_cancelled(&self, url: &str) {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(url)?;

            env.call_method(
                &self.jni_listener,
                REQUEST_DATASET_LISTENER_ON_PAGE_CANCELLED.0,
                REQUEST_DATASET_LISTENER_ON_PAGE_CANCELLED.1,
                &[JValue::Object(&url_obj)],
            )?;

            env.delete_local_ref(url_obj);

            Ok(())
        })
        .expect("Error in on_page_cancelled");
    }

    fn on_page_finished(&self, url: &str, document: &str) {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(url)?;
            let document_obj = env.new_string(document)?;

            env.call_method(
                &self.jni_listener,
                REQUEST_DATASET_LISTENER_ON_PAGE_FINISHED.0,
                REQUEST_DATASET_LISTENER_ON_PAGE_FINISHED.1,
                &[JValue::Object(&url_obj), JValue::Object(&document_obj)],
            )?;

            env.delete_local_ref(url_obj);
            env.delete_local_ref(document_obj);

            Ok(())
        })
        .expect("Error in on_page_finished");
    }
    fn on_page_error(&self, url: &str, error: &str) {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(url)?;
            let error_obj = env.new_string(error)?;

            env.call_method(
                &self.jni_listener,
                REQUEST_DATASET_LISTENER_ON_PAGE_ERROR.0,
                REQUEST_DATASET_LISTENER_ON_PAGE_ERROR.1,
                &[JValue::Object(&url_obj), JValue::Object(&error_obj)],
            )?;

            env.delete_local_ref(url_obj);
            env.delete_local_ref(error_obj);

            Ok(())
        })
        .expect("Error in on_page_error");
    }

    fn on_load_progress(&self, url: &str, progress: i32) {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(url)?;

            env.call_method(
                &self.jni_listener,
                REQUEST_DATASET_LISTENER_ON_LOAD_PROGRESS.0,
                REQUEST_DATASET_LISTENER_ON_LOAD_PROGRESS.1,
                &[JValue::Object(&url_obj), JValue::Int(progress)],
            )?;

            env.delete_local_ref(url_obj);

            Ok(())
        })
        .expect("Error in on_page_progress");
    }

    fn on_should_override_url_loading(&self, url: &str) {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(url)?;

            env.call_method(
                &self.jni_listener,
                REQUEST_DATASET_LISTENER_ON_SHOULD_OVERRIDE_URL_LOADING.0,
                REQUEST_DATASET_LISTENER_ON_SHOULD_OVERRIDE_URL_LOADING.1,
                &[JValue::Object(&url_obj)],
            )?;

            env.delete_local_ref(url_obj);

            Ok(())
        })
        .expect("Error in on_should_override_url_loading");
    }

    fn on_should_intercept_request(&self, url: &str) {
        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            let url_obj = env.new_string(url)?;

            env.call_method(
                &self.jni_listener,
                REQUEST_DATASET_LISTENER_ON_SHOULD_INTERCEPT_REQUEST.0,
                REQUEST_DATASET_LISTENER_ON_SHOULD_INTERCEPT_REQUEST.1,
                &[JValue::Object(&url_obj)],
            )?;

            env.delete_local_ref(url_obj);

            Ok(())
        })
        .expect("Error in uld_intercept_request");
    }
}
