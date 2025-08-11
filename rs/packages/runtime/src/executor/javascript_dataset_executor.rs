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
    fmt::Display,
    sync::{Arc, RwLock},
};

use crate::{
    executor::dataset_executor::DatasetExecutor,
    plugin_manager::PluginManager,
    web_engine::{WebEngineListener, WebEngineMut},
};

#[derive(Debug)]
enum WebEngineEvent {
    PageStarted(String),
    PageFinished(String),
    PageError(String),
    LoadProgress(i32),
}

impl Display for WebEngineEvent {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            WebEngineEvent::PageStarted(value) => write!(f, "PageStarted {}", value),
            WebEngineEvent::PageFinished(value) => write!(f, "PageFinished {}", value),
            WebEngineEvent::PageError(value) => write!(f, "PageError {}", value),
            WebEngineEvent::LoadProgress(value) => write!(f, "LoadProgress {}", value),
        }
    }
}

type WebEngineCallback = Box<dyn FnMut(WebEngineEvent) + Send + Sync>;

pub struct JavaScriptDatasetExecutor<'local> {
    id: &'local str,
    url: &'local str,
    js: &'local str,
}

impl<'local> JavaScriptDatasetExecutor<'local> {
    pub fn new(id: &'local str, url: &'local str, js: &'local str) -> Self {
        Self { id, url, js }
    }
}

impl<'local> DatasetExecutor for JavaScriptDatasetExecutor<'local> {
    fn request(&self) -> Result<String, String> {
        let plugin_manager = PluginManager::get_instance();
        let mut plugin_manager = plugin_manager.lock().map_err(|e| e.to_string())?;

        let webengine = plugin_manager.new_webengine()?;

        let callback: WebEngineCallback = Box::new(|e| {
            println!("WebEngineCallback {} ", e);
        });

        let listener = Arc::new(RwLock::new(WebEngineListenerImpl::new(
            self.url.to_string(),
            callback,
        )));
        webengine.write().unwrap().set_listener(listener);
        webengine.read().unwrap().load_url(self.url)?;

        Ok("".to_string())
    }
}

struct WebEngineListenerImpl {
    url: String,
    callback: WebEngineCallback,
}

impl WebEngineListenerImpl {
    fn new(url: String, callback: WebEngineCallback) -> Self {
        Self { url, callback }
    }
}

impl WebEngineListener for WebEngineListenerImpl {
    fn on_page_started(&mut self, engine: WebEngineMut, url: &str) {
        let callback = &mut self.callback;
        callback(WebEngineEvent::PageStarted(url.to_string()));

        println!("on_page_started {}", url)
    }

    fn on_page_finished(&mut self, engine: WebEngineMut, url: &str) {
        let callback = &mut self.callback;
        callback(WebEngineEvent::PageFinished(url.to_string()));

        println!("on_page_finished {}", url)
    }

    fn on_page_error(&mut self, engine: WebEngineMut, url: &str) {
        let callback = &mut self.callback;
        callback(WebEngineEvent::PageError(url.to_string()));

        println!("on_page_error {}", url)
    }

    fn on_load_progress(&mut self, engine: WebEngineMut, progress: i32) {
        let callback = &mut self.callback;
        callback(WebEngineEvent::LoadProgress(progress));

        println!("on_load_progress {}", progress)
    }

    fn should_override_url_loading(&mut self, engine: WebEngineMut, url: &str) -> bool {
        false
    }

    fn should_intercept_request(&mut self, engine: WebEngineMut, url: &str) -> Option<String> {
        None
    }
}
