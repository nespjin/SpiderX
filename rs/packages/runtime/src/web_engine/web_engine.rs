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

pub struct WebEngineListener {
    on_page_started: Option<Box<dyn FnMut(&str)>>,
    on_page_finished: Option<Box<dyn FnMut(&str)>>,
    on_page_error: Option<Box<dyn FnMut(&str)>>,
    on_load_progress: Option<Box<dyn FnMut(f64)>>,
    should_override_url_loading: Option<Box<dyn FnMut(&str) -> bool>>,
    should_intercept_request: Option<Box<dyn FnMut(&str) -> String>>,
}

impl WebEngineListener {
    pub fn new() -> Self {
        WebEngineListener {
            on_page_started: None,
            on_page_finished: None,
            on_page_error: None,
            on_load_progress: None,
            should_override_url_loading: None,
            should_intercept_request: None,
        }
    }
}

pub trait WebEngine {
    fn init(&mut self) -> Result<(), String>;

    fn load_url(&self, url: &str) -> Result<(), String>;

    fn load_data(&self, data: &str) -> Result<(), String>;

    fn reload(&self) -> Result<(), String>;

    fn evaluate(&self, script: &str) -> Result<String, String>;

    fn set_listener(&mut self, listener: WebEngineListener);

    fn destroy(&mut self) -> Result<(), String>;
}

pub struct SimpleWebEngine {
    listener: WebEngineListener,
}

impl WebEngine for SimpleWebEngine {
    fn init(&mut self) -> Result<(), String> {
        todo!()
    }

    fn load_url(&self, url: &str) -> Result<(), String> {
        todo!()
    }

    fn load_data(&self, data: &str) -> Result<(), String> {
        todo!()
    }

    fn reload(&self) -> Result<(), String> {
        todo!()
    }

    fn evaluate(&self, script: &str) -> Result<String, String> {
        todo!()
    }

    fn set_listener(&mut self, listener: WebEngineListener) {
        self.listener = listener;
    }

    fn destroy(&mut self) -> Result<(), String> {
        todo!()
    }
}
