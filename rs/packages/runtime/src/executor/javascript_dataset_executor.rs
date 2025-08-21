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
    sync::{
        Arc,
        mpsc::{self, RecvTimeoutError},
    },
    thread::{self},
    time::Duration,
};

use crate::{
    executor::dataset_executor::DatasetExecutor,
    utils::log_utils,
    web_engine::{
        web_engine::{WebEngineListener, WebEngineMut},
        web_engine_manager::WebEngineManager,
    },
};

#[derive(Debug, Clone)]
enum WebEngineEvent {
    PageStarted(String),
    PageFinished(String, String),
    PageCancelled(String),
    PageError(String, String),
    LoadProgress(i32),
    ReceivedError(String, String),
    ReceivedData(String, String),
}

impl Display for WebEngineEvent {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            WebEngineEvent::PageStarted(value) => write!(f, "PageStarted {}", value),
            WebEngineEvent::PageCancelled(value) => write!(f, "PageCancelled {}", value),
            WebEngineEvent::PageFinished(url, document) => {
                write!(f, "PageFinished {} {}", url, document)
            }
            WebEngineEvent::PageError(value, error) => write!(f, "PageError {} {}", value, error),
            WebEngineEvent::LoadProgress(value) => write!(f, "LoadProgress {}", value),
            WebEngineEvent::ReceivedError(value, error) => {
                write!(f, "ReceivedError {} {}", value, error)
            }
            WebEngineEvent::ReceivedData(value, data) => {
                write!(f, "ReceivedData {} {}", value, data)
            }
        }
    }
}

type WebEngineCallback = Box<dyn Fn(WebEngineEvent) + Send + Sync>;

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
        let (tx, rx) = mpsc::channel::<WebEngineEvent>();
        let webengine = {
            log_utils::logd(&format!(
                "JavaScriptDatasetExecutor::request on thread {:?}",
                thread::current().id()
            ));
            let mut wm = WebEngineManager::get_instance()
                .lock()
                .map_err(|e| e.to_string())?;
            wm.new_webengine()?
        };

        let callback: WebEngineCallback = Box::new(move |e| {
            log_utils::logd(&format!(
                "WebEngineCallback {} on thread {:?}",
                e.clone(),
                thread::current().id()
            ));
            tx.send(e).expect("Send message to channel failed.");
        });

        let listener = Arc::new(WebEngineListenerImpl::new(self.url.to_string(), callback));

        webengine.write().unwrap().set_listener(listener);
        webengine.read().unwrap().load_url(self.url)?;

        let mut result: Result<String, String> = Ok("".to_string());

        loop {
            match rx.recv_timeout(Duration::from_secs(10)) {
                Ok(received) => {
                    log_utils::logd(&format!(
                        "JavaScriptDatasetExecutor::request received {} on thread {:?}",
                        &received,
                        thread::current().id()
                    ));
                    match received {
                        WebEngineEvent::PageFinished(url, document) => {
                            if url == self.url {
                                let ret = webengine.write().unwrap().evaluate(self.js)?;
                                result = Ok(ret);
                                break;
                            }
                        }
                        WebEngineEvent::PageError(url, error) => {
                            if url == self.url {
                                result = Err(error.to_string());
                                break;
                            }
                        }
                        _ => (),
                    }
                }
                Err(RecvTimeoutError::Timeout) => {
                    log_utils::logd(&format!("JavaScriptDatasetExecutor::request timeout"));
                    result = Err("JavaScriptDatasetExecutor::request timeout".to_string());
                    break;
                }
                Err(RecvTimeoutError::Disconnected) => {
                    log_utils::logd(&format!("JavaScriptDatasetExecutor::request disconnected"));
                    result = Err("JavaScriptDatasetExecutor::request disconnected".to_string());
                    break;
                }
            }
            thread::sleep(Duration::from_millis(80));
        }

        {
            let mut wm = WebEngineManager::get_instance()
                .lock()
                .map_err(|e| e.to_string())?;

            // Release lock
            let id = { webengine.read().unwrap().id() };
            wm.remove_webengine(id)?;
        }

        log_utils::logd(&format!(
            "JavaScriptDatasetExecutor::request end {} {}",
            self.id, self.url
        ));

        return result;
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
    fn on_page_started(&self, engine: WebEngineMut, url: &str) {
        let callback = &self.callback;
        callback(WebEngineEvent::PageStarted(url.to_string()));

        log_utils::logd(&format!("on_page_started {}", url))
    }

    fn on_page_cancelled(&self, engine: WebEngineMut, url: &str) {
        let callback = &self.callback;
        callback(WebEngineEvent::PageCancelled(url.to_string()));

        log_utils::logd(&format!("on_page_cancelled {}", url))
    }

    fn on_page_finished(&self, engine: WebEngineMut, url: &str, document: &str) {
        let callback = &self.callback;
        callback(WebEngineEvent::PageFinished(
            url.to_string(),
            document.to_string(),
        ));

        log_utils::logd(&format!("on_page_finished {} {}", url, document))
    }

    fn on_page_error(&self, engine: WebEngineMut, url: &str, error: &str) {
        let callback = &self.callback;
        callback(WebEngineEvent::PageError(
            url.to_string(),
            error.to_string(),
        ));

        log_utils::logd(&format!("on_page_error {} {}", url, error))
    }

    fn on_load_progress(&self, engine: WebEngineMut, progress: i32) {
        let callback = &self.callback;
        callback(WebEngineEvent::LoadProgress(progress));

        log_utils::logd(&format!("on_load_progress {}", progress))
    }

    fn should_override_url_loading(&self, engine: WebEngineMut, url: &str) -> bool {
        true
    }

    fn should_intercept_request(&self, engine: WebEngineMut, url: &str) -> Option<String> {
        Some("ShouldInterceptRequest in Rust".to_string())
    }

    fn on_received_data(&self, engine: WebEngineMut, url: &str, data: &str) {
        let callback = &self.callback;
        callback(WebEngineEvent::ReceivedData(
            url.to_string(),
            data.to_string(),
        ));

        log_utils::logd(&format!("on_receive_data {} {}", url, data))
    }

    fn on_received_error(&self, engine: WebEngineMut, url: &str, error: &str) {
        let callback = &self.callback;
        callback(WebEngineEvent::ReceivedError(
            url.to_string(),
            error.to_string(),
        ));

        log_utils::logd(&format!("on_receive_error {} {}", url, error))
    }
}
