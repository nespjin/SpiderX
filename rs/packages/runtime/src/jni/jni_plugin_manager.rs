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
use std::sync::Mutex;
use std::sync::OnceLock;

use crate::device::device_manager::DeviceManager;
use crate::jni::jni_classes;
use crate::jni::jni_handler::JniHandler;
use crate::jni::jni_methods;
use crate::jni::jni_plugin::JniPlugin;
use crate::jni::jni_screen_type::JniScreenType;
use crate::jni::jni_webview::JAVA_WEBVIEW_CLASS;
use crate::plugin_manager::PluginManager;
use crate::plugin_manager::PluginManagerConfig;
use crate::plugin_manager::PluginSource;
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
) -> jstring {
    let mut handler = JniHandler::new_with_env(env);
    let plugin_id = handler.get_string(&pluginId);
    let dataset_id = handler.get_string(&datasetId);

    let data = {
        let plugin_manager = PluginManager::get_instance().lock().unwrap();
        plugin_manager.request_dataset(&plugin_id, &dataset_id)
    };

    log_utils::logd(&format!(
        "Java_com_nesp_spiderx_runtime_PluginManager_nativeRequestDataset {} {}",
        plugin_id, dataset_id
    ));

    // handler
    //     .throw_java_expception_if_error(data)
    data.map(|e| handler.new_string(&e))
        .map(|e| e.into_raw())
        .unwrap_or(JObject::null().into_raw())
}
