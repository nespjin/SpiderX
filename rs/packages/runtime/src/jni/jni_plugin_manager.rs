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
use std::collections::HashMap;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::OnceLock;
use std::sync::RwLock;

use crate::jni::jni_webview::JNI_WV_JAVA_FIELD_NAME_PTR;
use crate::jni::jni_webview::JniWebView;
use crate::plugin_manager::PluginManager;
use crate::plugin_manager::PluginManagerConfig;

pub(crate) struct JniPluginManager {
    wv_java_class: Option<JClass<'static>>,
    java_vm: Option<JavaVM>,
    wv_id: i64,
    webviews: Arc<RwLock<HashMap<i64, Arc<JniWebView>>>>,
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
        let wv_java_obj = self
            .java_env()
            .unwrap()
            .new_global_ref(self.new_wv_obj().unwrap())
            .unwrap();

        let id: i64 = self.wv_id;

        self.java_env()
            .unwrap()
            .set_field(
                wv_java_obj.clone(),
                JNI_WV_JAVA_FIELD_NAME_PTR,
                "J",
                JValueGen::Long(id),
            )
            .unwrap();

        let wv = Arc::new(JniWebView::new(id, wv_java_obj));
        self.webviews.write().unwrap().insert(id, wv);

        self.wv_id = id;

        Ok(id)
    }

    pub fn get_jni_wv(&self, id: i64) -> Result<Arc<JniWebView>, String> {
        Ok(self.webviews.read().unwrap().get(&id).unwrap().clone())
    }
}

#[unsafe(no_mangle)]
pub unsafe extern "C" fn Java_com_nesp_spiderx_runtime_PluginManager_nativeInit(
    mut env: JNIEnv,
    _this: JObject,
    databasePath: JString,
    webviewClass: JClass<'static>,
) {
    let database_path_java_str = env
        .get_string(&databasePath)
        .expect("Can't get java string");
    let database_path = String::from(database_path_java_str);

    let config = PluginManagerConfig { database_path };
    let plugin_manager = PluginManager::get_instance();
    let mut plugin_manager = plugin_manager.lock().unwrap();

    let plugin_init_ret = plugin_manager.init(config);
    if let Err(e) = plugin_init_ret {
        env.throw_new(
            "java/lang/Exception",
            format!("Init PluginManger failed {}", e.to_string()),
        )
        .expect("throw failed when PluginManger init failed");
    }

    let jni_plugin_manager = JniPluginManager::get_instance();
    let mut jni_plugin_manager = jni_plugin_manager.lock().unwrap();

    let java_vm = env.get_java_vm().expect("Get java vm failed");
    let jni_plugin_manager_init_ret = jni_plugin_manager.init(java_vm, webviewClass);
    if let Err(e) = jni_plugin_manager_init_ret {
        env.throw_new(
            "java/lang/Exception",
            &format!("Set WebView Java Class failed {}", e.to_string()),
        )
        .expect("throw failed when Set WebView Java Class failed");
    }
}
