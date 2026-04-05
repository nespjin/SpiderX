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
    executor::{
        dataset_executor::DatasetExecutor,
        request_dataset_listener::RequestJavaScriptDatasetListenerArc,
        request_javascript_dataset_config::RequestJavaScriptDatasetConfigArc,
    },
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
    LoadProgress(String, i32),
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
            WebEngineEvent::LoadProgress(url, value) => write!(f, "LoadProgress {} {}", url, value),
            WebEngineEvent::ReceivedError(url, error) => {
                write!(f, "ReceivedError {} {}", url, error)
            }
            WebEngineEvent::ReceivedData(url, data) => {
                write!(f, "ReceivedData {} {}", url, data)
            }
        }
    }
}

type WebEngineCallback = Box<dyn Fn(WebEngineEvent) + Send + Sync>;

pub struct JavaScriptDatasetExecutor<'local> {
    id: &'local str,
    url: &'local str,
    js: &'local str,
    listener: Option<RequestJavaScriptDatasetListenerArc>,
    config: Option<RequestJavaScriptDatasetConfigArc>,
}

impl<'local> JavaScriptDatasetExecutor<'local> {
    pub fn new(
        id: &'local str,
        url: &'local str,
        js: &'local str,
        listener: Option<RequestJavaScriptDatasetListenerArc>,
        config: Option<RequestJavaScriptDatasetConfigArc>,
    ) -> Self {
        Self {
            id,
            url,
            js,
            listener,
            config,
        }
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

        let listener = Arc::new(WebEngineListenerImpl::new(
            self.url.to_string(),
            self.listener.clone(),
            self.config.clone(),
            callback,
        ));

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
                        WebEngineEvent::PageFinished(url, _document) => {
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
    listener: Option<RequestJavaScriptDatasetListenerArc>,
    config: Option<RequestJavaScriptDatasetConfigArc>,
    callback: WebEngineCallback,
}

impl WebEngineListenerImpl {
    fn new(
        url: String,
        listener: Option<RequestJavaScriptDatasetListenerArc>,
        config: Option<RequestJavaScriptDatasetConfigArc>,
        callback: WebEngineCallback,
    ) -> Self {
        Self {
            url,
            listener,
            config,
            callback,
        }
    }
}

impl WebEngineListener for WebEngineListenerImpl {
    fn on_page_started(&self, _engine: WebEngineMut, url: &str) {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_page_started(url);
        }

        let callback = &self.callback;
        callback(WebEngineEvent::PageStarted(url.to_string()));

        log_utils::logd(&format!("on_page_started {}", url))
    }

    fn on_page_cancelled(&self, _engine: WebEngineMut, url: &str) {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_page_cancelled(url);
        }

        let callback = &self.callback;
        callback(WebEngineEvent::PageCancelled(url.to_string()));

        log_utils::logd(&format!("on_page_cancelled {}", url))
    }

    fn on_page_finished(&self, _engine: WebEngineMut, url: &str, document: &str) {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_page_finished(url, document);
        }

        let callback = &self.callback;
        callback(WebEngineEvent::PageFinished(
            url.to_string(),
            document.to_string(),
        ));

        log_utils::logd(&format!("on_page_finished {} {}", url, ""))
    }

    fn on_page_error(&self, _engine: WebEngineMut, url: &str, error: &str) {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_page_error(url, error);
        }

        let callback = &self.callback;
        callback(WebEngineEvent::PageError(
            url.to_string(),
            error.to_string(),
        ));

        log_utils::logd(&format!("on_page_error {} {}", url, error))
    }

    fn on_load_progress(&self, _engine: WebEngineMut, url: &str, progress: i32) {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_load_progress(url, progress);
        }

        let callback = &self.callback;
        callback(WebEngineEvent::LoadProgress(url.to_string(), progress));

        log_utils::logd(&format!("on_load_progress {} {}", url, progress))
    }

    fn should_override_url_loading(&self, _engine: WebEngineMut, url: &str) -> bool {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_should_override_url_loading(url);
        }

        let config = &self.config;
        if let Some(config) = config.as_ref() {
            if let Some(ret) = config.should_override_url_loading(url) {
                return ret;
            }
        }
        log_utils::logd(&format!("should_override_url_loading {}", url));
        false
    }

    fn should_intercept_request(&self, _engine: WebEngineMut, url: &str) -> Option<String> {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_should_intercept_request(url);
        }

        let config = &self.config;
        if let Some(config) = config.as_ref() {
            if let Some(ret) = config.should_intercept_request(url) {
                return Some(ret);
            }
        }
        log_utils::logd(&format!("should_intercept_request {}", url));
        None
    }

    fn on_received_data(&self, _engine: WebEngineMut, url: &str, data: &str) {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_receive_data(url, data);
        }

        let callback = &self.callback;
        callback(WebEngineEvent::ReceivedData(
            url.to_string(),
            data.to_string(),
        ));

        log_utils::logd(&format!("on_receive_data {} {}", url, data))
    }

    fn on_received_error(&self, _engine: WebEngineMut, url: &str, error: &str) {
        let listener = &self.listener;
        if let Some(listener) = listener.as_ref() {
            listener.on_receive_error(url, error);
        }

        let callback = &self.callback;
        callback(WebEngineEvent::ReceivedError(
            url.to_string(),
            error.to_string(),
        ));

        log_utils::logd(&format!("on_receive_error {} {}", url, error))
    }
}
