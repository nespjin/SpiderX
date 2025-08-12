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
    sync::{Arc, Mutex, OnceLock, RwLock},
};

use crate::{jni::jni_webview, web_engine::web_engine::WebEngineMut};

const MAX_WV_POOL_SIZE: usize = 10;

pub struct WebEngineManager {
    webengine_id: i64,
    webengines: Arc<RwLock<HashMap<i64, WebEngineMut>>>,
    webengine_pool: Arc<RwLock<Vec<WebEngineMut>>>,
    is_cache_engine: bool,
}

impl WebEngineManager {
    pub fn get_instance() -> &'static Mutex<WebEngineManager> {
        static INSTANCE: OnceLock<Mutex<WebEngineManager>> = OnceLock::new();
        INSTANCE.get_or_init(|| {
            Mutex::new(WebEngineManager {
                webengine_id: 0,
                webengines: Arc::new(RwLock::new(HashMap::new())),
                webengine_pool: Arc::new(RwLock::new(Vec::new())),
                is_cache_engine: false,
            })
        })
    }

    pub fn init(&mut self, is_cache_engine: bool) -> Result<(), String> {
        self.is_cache_engine = is_cache_engine;
        Ok(())
    }

    pub fn is_cache_engine(&self) -> bool {
        self.is_cache_engine
    }

    pub fn new_webengine(&mut self) -> Result<WebEngineMut, String> {
        let id: i64 = self.webengine_id;
        let next_id = self.webengine_id + 1;

        let engine = if !self
            .webengine_pool
            .read()
            .map_err(|e| e.to_string())?
            .is_empty()
        {
            let engine = self
                .webengine_pool
                .write()
                .map_err(|e| e.to_string())?
                .remove(0);
            engine.write().map_err(|e| e.to_string())?.set_id(id);
            engine
        } else {
            // TODO: Add other platform impl
            Arc::new(RwLock::new(jni_webview::new_jni_wv(id)?))
        };

        println!("Create WebEngine {}", id);
        engine.write().unwrap().init()?;

        self.webengine_id = next_id;
        self.webengines
            .write()
            .map_err(|e| e.to_string())?
            .insert(id, engine.clone());
        Ok(engine)
    }

    pub fn is_webengine_exists(&self, id: i64) -> bool {
        self.webengines.read().unwrap().contains_key(&id)
    }

    pub fn get_webengine(&self, id: i64) -> Option<WebEngineMut> {
        self.webengines
            .read()
            .unwrap()
            .get(&id)
            .map(|wv| wv.clone())
    }

    pub fn remove_webengine(&mut self, id: i64) -> Result<(), String> {
        let engine = self
            .webengines
            .write()
            .map_err(|e| e.to_string())?
            .remove(&id);

        if let Some(engine) = engine {
            engine.write().unwrap().destroy()?;

            let is_pool_not_full = self
                .webengine_pool
                .write()
                .map_err(|e| e.to_string())?
                .len()
                < MAX_WV_POOL_SIZE;
            if self.is_cache_engine && is_pool_not_full {
                self.webengine_pool
                    .write()
                    .map_err(|e| e.to_string())?
                    .push(engine);
            }
        }

        Ok(())
    }
}
