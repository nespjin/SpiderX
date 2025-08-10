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

use jni::AttachGuard;
use jni::JNIEnv;
use jni::JavaVM;
use jni::objects::*;
use jni::sys::JNI_FALSE;
use jni::sys::jboolean;
use jni::sys::jint;
use jni::sys::jobject;
use jni::sys::jstring;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::OnceLock;

use crate::jni::jni_constants::JAVA_CLASS_NAME_ARRAY_LIST;
use crate::jni::jni_constants::JAVA_METHOD_NAME_LIST_ADD;
use crate::jni::jni_constants::JAVA_METHOD_SIG_LIST_ADD;
use crate::jni::jni_obj_plugin;
use crate::jni::jni_obj_screen_type;
use crate::jni::jni_utils;
use crate::plugin_manager::PluginManager;
use crate::plugin_manager::PluginManagerConfig;
use crate::plugin_manager::PluginSource;

const MAX_WV_POOL_SIZE: usize = 10;

pub(crate) const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON: jint = 0;
pub(crate) const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE: jint = 1;

pub(crate) struct JniPluginManager {
    wv_java_class: Option<JClass<'static>>,
    java_vm: Option<JavaVM>,
}

impl JniPluginManager {
    pub(crate) fn get_instance() -> Arc<Mutex<JniPluginManager>> {
        static INSTANCE: OnceLock<Arc<Mutex<JniPluginManager>>> = OnceLock::new();
        INSTANCE
            .get_or_init(|| {
                Arc::new(Mutex::new(JniPluginManager {
                    wv_java_class: None,
                    java_vm: None,
                }))
            })
            .clone()
    }

    pub fn init(&mut self, java_vm: JavaVM, wv_java_class: JClass<'static>) -> Result<(), String> {
        if self.wv_java_class.is_some() {
            return Err("The jni plugin manager is already init.".to_string());
        }
        self.java_vm = Some(java_vm);
        self.wv_java_class = Some(wv_java_class);
        Ok(())
    }

    pub fn wv_java_class(&self) -> Result<&JClass<'static>, String> {
        if self.wv_java_class.is_none() {
            return Err(
                "The webview java class is null. Please call PluginManger.init() first."
                    .to_string(),
            );
        }
        Ok(self.wv_java_class.as_ref().unwrap())
    }

    pub fn java_vm(&self) -> Result<&JavaVM, String> {
        if self.java_vm.is_none() {
            return Err(
                "The webview java vm is null. Please call PluginManger.init() first.".to_string(),
            );
        }
        Ok(self.java_vm.as_ref().unwrap())
    }

    #[inline]
    pub fn java_env(&self) -> Result<AttachGuard, String> {
        Ok(self
            .java_vm()
            .map_err(|e| e.to_string())?
            .attach_current_thread()
            .map_err(|e| e.to_string())?)
    }

    pub fn new_wv_obj(&self) -> Result<JObject, String> {
        let ctor_sig = if cfg!(target_os = "android") {
            "(Landroid/content/Context;)V"
        } else {
            "()V"
        };

        let wv_class = self.wv_java_class()?;
        let mut env = self.java_env()?;
        let wv_obj = env
            .new_object(wv_class, ctor_sig, &[])
            .map_err(|e| e.to_string())?;

        Ok(wv_obj)
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInit(
    mut env: JNIEnv,
    _this: JObject,
    databasePath: JString,
    screenType: JObject,
    webviewClass: JClass<'static>,
) {
    let database_path: String = env
        .get_string(&databasePath)
        .expect("get database path failed")
        .into();

    let screen_type = jni_obj_screen_type::java_object_to_screen_type(screenType);

    let config = PluginManagerConfig {
        database_path,
        screen_type,
    };
    let plugin_manager = PluginManager::get_instance();
    let mut plugin_manager = plugin_manager.lock().unwrap();

    jni_utils::throw_java_expception_if_error(&mut env, plugin_manager.init(config));

    let jni_plugin_manager = JniPluginManager::get_instance();
    let mut jni_plugin_manager = jni_plugin_manager.lock().unwrap();

    let java_vm = env.get_java_vm().expect("get java vm failed");
    jni_utils::throw_java_expception_if_error(
        &mut env,
        jni_plugin_manager.init(java_vm, webviewClass),
    );
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeSetScreenType(
    mut env: JNIEnv,
    _this: JObject,
    screenType: JObject,
) {
    let screen_type = jni_obj_screen_type::java_object_to_screen_type(screenType);

    let plugin_manager = PluginManager::get_instance();
    let mut plugin_manager = plugin_manager.lock().unwrap();

    jni_utils::throw_java_expception_if_error(
        &mut env,
        plugin_manager.set_screen_type(screen_type),
    );
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInstallPlugin(
    mut env: JNIEnv,
    _this: JObject,
    sourceType: jint,
    source: JByteArray,
) {
    unsafe {
        let array_len = env.get_array_length(&source).expect("get array len failed");

        let mut buf = vec![0; array_len as usize];
        env.get_byte_array_region(&source, 0, &mut buf)
            .expect("get byte array region failed");
        let buf: Vec<u8> = std::mem::transmute(buf);

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
                jni_utils::throw_java_expception_msg(
                    &mut env,
                    &format!("Invalid source type {}.", sourceType),
                );
                return;
            }
        };

        let plugin_manager = PluginManager::get_instance();
        let plugin_manager = plugin_manager.lock().unwrap();
        jni_utils::throw_java_expception_if_error(
            &mut env,
            plugin_manager.install_plugin(&plugin_source),
        );
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeIsPluginInstalled(
    mut env: JNIEnv,
    _this: JObject,
    id: JString,
) -> jboolean {
    let id: String = env.get_string(&id).expect("get id failed").into();

    let plugin_manager = PluginManager::get_instance();
    let plugin_manager = plugin_manager.lock().unwrap();
    let is_installed = plugin_manager.is_plugin_installed(&id);

    match jni_utils::throw_java_expception_if_error(&mut env, is_installed) {
        Some(is_installed) => is_installed.into(),
        None => return JNI_FALSE,
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeGetInstalledPlugin(
    mut env: JNIEnv,
    _this: JObject,
    id: JString,
) -> jobject {
    let id: String = env.get_string(&id).expect("get id failed").into();

    let plugin_manager = PluginManager::get_instance();
    let plugin_manager = plugin_manager.lock().unwrap();

    let plugin = match jni_utils::throw_java_expception_if_error(
        &mut env,
        plugin_manager.get_installed_plugin(&id),
    ) {
        Some(p) => {
            if let Some(plugin) = p {
                plugin
            } else {
                return JObject::null().into_raw();
            }
        }
        None => return JObject::null().into_raw(),
    };

    jni_obj_plugin::new(
        &mut env,
        &plugin.id,
        &plugin.name,
        &plugin.author,
        &plugin.version,
        &plugin.runtime_version,
        &plugin.description,
        &plugin.tags,
        &plugin.supported_screen_types,
        &plugin.datasets,
    )
    .unwrap()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeGetInstalledPlugins(
    mut env: JNIEnv,
    _this: JObject,
) -> jobject {
    let plugin_manager = PluginManager::get_instance();
    let plugin_manager = plugin_manager.lock().unwrap();

    let plugins = match jni_utils::throw_java_expception_if_error(
        &mut env,
        plugin_manager.get_installed_plugins(),
    ) {
        Some(p) => p,
        None => return JObject::null().into_raw(),
    };

    let arr_list = env
        .new_object(JAVA_CLASS_NAME_ARRAY_LIST, "()V", &[])
        .expect("unable to new array list");

    let mut plugin_objs = vec![];
    for plugin in plugins {
        plugin_objs.push(
            jni_obj_plugin::new(
                &mut env,
                &plugin.id,
                &plugin.name,
                &plugin.author,
                &plugin.version,
                &plugin.runtime_version,
                &plugin.description,
                &plugin.tags,
                &plugin.supported_screen_types,
                &plugin.datasets,
            )
            .unwrap(),
        );
    }

    for plugin_obj_ptr in plugin_objs {
        let plugin_obj = unsafe { JObject::from_raw(plugin_obj_ptr) };
        env.call_method(
            &arr_list,
            JAVA_METHOD_NAME_LIST_ADD,
            JAVA_METHOD_SIG_LIST_ADD,
            &[JValueGen::Object(&plugin_obj)],
        )
        .unwrap();
    }

    arr_list.into_raw()
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeUninstallPlugin(
    mut env: JNIEnv,
    _this: JObject,
    id: JString,
) {
    let id: String = env.get_string(&id).expect("get id failed").into();

    let plugin_manager = PluginManager::get_instance();
    let mut plugin_manager = plugin_manager.lock().unwrap();
    jni_utils::throw_java_expception_if_error(&mut env, plugin_manager.uninstall_plugin(&id));
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeRequestDataset(
    mut env: JNIEnv,
    _this: JObject,
    pluginId: JString,
    datasetId: JString,
) -> jstring {
    let plugin_id: String = env
        .get_string(&pluginId)
        .expect("get plugin id failed")
        .into();
    let dataset_id: String = env
        .get_string(&datasetId)
        .expect("get dataset id failed")
        .into();

    let plugin_manager = PluginManager::get_instance();
    let plugin_manager = plugin_manager.lock().unwrap();

    let data = plugin_manager.request_dataset(&plugin_id, &dataset_id);
    let data = jni_utils::throw_java_expception_if_error(&mut env, data);

    match data {
        Some(data) => {
            let data = env.new_string(&data).expect("create java string failed");
            data.into_raw()
        }
        None => JObject::null().into_raw(),
    }
}
