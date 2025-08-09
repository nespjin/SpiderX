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
use jni::sys::jint;
use std::collections::HashMap;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::OnceLock;
use std::sync::RwLock;

use crate::jni::jni_utils;
use crate::jni::jni_webview::JNI_WV_JAVA_FIELD_NAME_PTR;
use crate::jni::jni_webview::JniWebView;
use crate::plugin_manager::PluginManager;
use crate::plugin_manager::PluginManagerConfig;
use crate::plugin_manager::PluginSource;

const MAX_WV_POOL_SIZE: usize = 10;

pub(crate) const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON: jint = 0;
pub(crate) const JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE: jint = 1;

pub(crate) struct JniPluginManager {
    wv_java_class: Option<JClass<'static>>,
    java_vm: Option<JavaVM>,
    wv_id: i64,
    webviews: Arc<RwLock<HashMap<i64, Arc<JniWebView>>>>,
    wv_pool: Arc<RwLock<Vec<Arc<JniWebView>>>>,
}

impl JniPluginManager {
    pub(crate) fn get_instance() -> Arc<Mutex<JniPluginManager>> {
        static INSTANCE: OnceLock<Arc<Mutex<JniPluginManager>>> = OnceLock::new();
        INSTANCE
            .get_or_init(|| {
                Arc::new(Mutex::new(JniPluginManager {
                    wv_java_class: None,
                    java_vm: None,
                    wv_id: 0,
                    webviews: Arc::new(RwLock::new(HashMap::new())),
                    wv_pool: Arc::new(RwLock::new(Vec::new())),
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

    pub fn new_jni_wv(&mut self) -> Result<i64, String> {
        let id: i64 = self.wv_id;
        let next_id = id + 1;
        self.wv_id = next_id;

        let wv = if !self.wv_pool.read().map_err(|e| e.to_string())?.is_empty() {
            self.wv_pool.write().map_err(|e| e.to_string())?.remove(0)
        } else {
            let wv_java_obj = self
                .java_env()
                .map_err(|e| e.to_string())?
                .new_global_ref(self.new_wv_obj().map_err(|e| e.to_string())?)
                .map_err(|e| e.to_string())?;

            self.java_env()
                .map_err(|e| e.to_string())?
                .set_field(
                    wv_java_obj.clone(),
                    JNI_WV_JAVA_FIELD_NAME_PTR,
                    "J",
                    JValueGen::Long(id),
                )
                .map_err(|e| e.to_string())?;

            Arc::new(JniWebView::new(id, wv_java_obj))
        };

        self.webviews
            .write()
            .map_err(|e| e.to_string())?
            .insert(id, wv);

        Ok(id)
    }

    pub fn is_jni_wv_exist(&self, id: i64) -> bool {
        self.webviews
            .read()
            .map(|e| e.contains_key(&id))
            .unwrap_or(false)
    }

    pub fn get_jni_wv(&self, id: i64) -> Result<Option<Arc<JniWebView>>, String> {
        Ok(self
            .webviews
            .read()
            .map_err(|e| e.to_string())?
            .get(&id)
            .map(|e| e.clone())
            .clone())
    }

    pub fn remove_jni_wv(&mut self, id: i64) -> Result<(), String> {
        let wv = self
            .webviews
            .write()
            .map_err(|e| e.to_string())?
            .remove(&id);

        if let Some(wv) = wv {
            if self.wv_pool.write().map_err(|e| e.to_string())?.len() < MAX_WV_POOL_SIZE {
                self.wv_pool.write().map_err(|e| e.to_string())?.push(wv);
            }
        }

        Ok(())
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInit(
    mut env: JNIEnv,
    _this: JObject,
    databasePath: JString,
    webviewClass: JClass<'static>,
) {
    let database_path = env.get_string(&databasePath).map_err(|e| e.to_string());
    let database_path = match jni_utils::throw_java_expception_if_error(&mut env, database_path) {
        Some(path) => String::from(path),
        None => return,
    };

    let config = PluginManagerConfig { database_path };
    let plugin_manager = PluginManager::get_instance();
    let mut plugin_manager = plugin_manager.lock().unwrap();

    if jni_utils::throw_java_expception_if_error(&mut env, plugin_manager.init(config)).is_none() {
        return;
    }

    let jni_plugin_manager = JniPluginManager::get_instance();
    let mut jni_plugin_manager = jni_plugin_manager.lock().unwrap();

    let java_vm = env.get_java_vm().map_err(|e| e.to_string());
    jni_utils::throw_java_expception_if_error(&mut env, java_vm)
        .map(|e| jni_plugin_manager.init(e, webviewClass))
        .map(|e| jni_utils::throw_java_expception_if_error(&mut env, e));
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInstallPlugin(
    mut env: JNIEnv,
    _this: JObject,
    sourceType: jint,
    source: JByteArray,
) {
    unsafe {
        let array_len = env.get_array_length(&source).map_err(|e| e.to_string());
        let array_len = jni_utils::throw_java_expception_if_error(&mut env, array_len);
        let array_len = match array_len {
            Some(len) => len,
            None => return,
        };

        let mut buf = vec![0; array_len as usize];
        let ret = env
            .get_byte_array_region(&source, 0, &mut buf)
            .map_err(|e| e.to_string());
        jni_utils::throw_java_expception_if_error(&mut env, ret);
        let buf: Vec<u8> = std::mem::transmute(buf);

        let plugin_source = match sourceType {
            JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON | JNI_PLUGIN_SOURCE_TYPE_MANIFEST_JSON_FILE => {
                let source = String::from_utf8(buf).map_err(|e| e.to_string());
                let source = jni_utils::throw_java_expception_if_error(&mut env, source);
                let source = match source {
                    Some(source) => source,
                    None => return,
                };
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
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeUninstallPlugin(
    mut env: JNIEnv,
    _this: JObject,
    id: JString,
) {
    let id = env.get_string(&id).map_err(|e| e.to_string());
    let id = match jni_utils::throw_java_expception_if_error(&mut env, id) {
        Some(path) => String::from(path),
        None => return,
    };

    let plugin_manager = PluginManager::get_instance();
    let mut plugin_manager = plugin_manager.lock().unwrap();
    jni_utils::throw_java_expception_if_error(&mut env, plugin_manager.uninstall_plugin(&id));
}
