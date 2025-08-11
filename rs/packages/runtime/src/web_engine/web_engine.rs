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

use std::sync::{Arc, RwLock};

pub type RwLockWebEngine = RwLock<dyn WebEngine>;
pub type WebEngineMut = Arc<RwLockWebEngine>;

pub trait WebEngine: Send + Sync {
    fn init(&mut self) -> Result<(), String>;

    fn set_id(&mut self, id: i64);

    fn id(&self) -> i64;

    fn load_url(&self, url: &str) -> Result<(), String>;

    fn load_data(&self, data: &str) -> Result<(), String>;

    fn reload(&self) -> Result<(), String>;

    fn evaluate(&self, script: &str) -> Result<String, String>;

    // fn add_listener(&mut self, listener: Arc<dyn WebEngineListener>) -> i64;

    // fn remove_listener(&mut self, id: i64);

    // fn notify_listeners<F>(&self, callback: F)
    // where
    //     Self: Sized,
    //     F: FnMut(Arc<dyn WebEngineListener>);

    // fn listeners(&self) -> Vec<Arc<dyn WebEngineListener>>;

    fn set_listener(&mut self, listener: Arc<dyn WebEngineListener>);

    fn listener(&self) -> Option<Arc<dyn WebEngineListener>>;

    fn destroy(&mut self) -> Result<(), String>;
}

pub trait WebEngineListener: Send + Sync {
    fn on_page_started(&mut self, engine: WebEngineMut, url: &str);

    fn on_page_finished(&mut self, engine: WebEngineMut, url: &str);

    fn on_page_error(&mut self, engine: WebEngineMut, url: &str);

    fn on_load_progress(&mut self, engine: WebEngineMut, progress: i32);

    fn should_override_url_loading(&mut self, engine: WebEngineMut, url: &str) -> bool;

    fn should_intercept_request(&mut self, engine: WebEngineMut, url: &str) -> Option<String>;
}
