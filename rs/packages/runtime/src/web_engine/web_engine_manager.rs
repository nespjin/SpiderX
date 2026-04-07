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
    collections::{HashMap, VecDeque},
    sync::{
        Arc, OnceLock, RwLock,
        atomic::{AtomicBool, AtomicI64, Ordering},
    },
};

use crate::{jni::jni_webview, web_engine::web_engine::WebEngineMut};

const MAX_WV_POOL_SIZE: usize = 10;

pub struct WebEngineManager {
    webengine_id: Arc<AtomicI64>,
    webengines: Arc<RwLock<HashMap<i64, WebEngineMut>>>,
    webengine_pool: Arc<RwLock<VecDeque<WebEngineMut>>>,
    is_cache_engine: Arc<AtomicBool>,
}

impl WebEngineManager {
    pub fn get_instance() -> &'static WebEngineManager {
        static INSTANCE: OnceLock<WebEngineManager> = OnceLock::new();
        INSTANCE.get_or_init(|| WebEngineManager {
            webengine_id: Arc::new(AtomicI64::new(0)),
            webengines: Arc::new(RwLock::new(HashMap::new())),
            webengine_pool: Arc::new(RwLock::new(VecDeque::new())),
            is_cache_engine: Arc::new(AtomicBool::new(false)),
        })
    }

    pub fn init(&self, is_cache_engine: bool) -> Result<(), String> {
        self.is_cache_engine
            .store(is_cache_engine, Ordering::SeqCst);
        Ok(())
    }

    pub fn is_cache_engine(&self) -> bool {
        self.is_cache_engine.load(Ordering::SeqCst)
    }

    pub fn new_webengine(&self) -> Result<WebEngineMut, String> {
        let id = self.webengine_id.fetch_add(1, Ordering::SeqCst);

        let engine = if !self
            .webengine_pool
            .read()
            .map_err(|e| e.to_string())?
            .is_empty()
        {
            // O(1) removal from front with VecDeque
            let engine = self
                .webengine_pool
                .write()
                .map_err(|e| e.to_string())?
                .pop_front();
            let engine = engine.expect("Pool indicated non-empty but pop_front returned None");
            engine.write().map_err(|e| e.to_string())?.set_id(id);
            engine
        } else {
            // TODO: Add other platform impl
            Arc::new(RwLock::new(jni_webview::new_jni_webview(id)?))
        };

        {
            engine.write().map_err(|e| e.to_string())?.init()?;
        }

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

    pub fn remove_webengine(&self, id: i64) -> Result<(), String> {
        let engine = {
            self.webengines
                .write()
                .map_err(|e| e.to_string())?
                .remove(&id)
        };

        if let Some(engine) = engine {
            {
                engine.write().unwrap().remove_listener();
            }
            let is_pool_not_full =
                self.webengine_pool.read().map_err(|e| e.to_string())?.len() < MAX_WV_POOL_SIZE;
            if self.is_cache_engine.load(Ordering::SeqCst) && is_pool_not_full {
                self.webengine_pool
                    .write()
                    .map_err(|e| e.to_string())?
                    .push_back(engine);
            } else {
                engine.write().unwrap().destroy()?;
            }
        }

        Ok(())
    }
}
