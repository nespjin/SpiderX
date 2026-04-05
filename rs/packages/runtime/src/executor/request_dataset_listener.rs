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
use std::{any::Any, sync::Arc};

pub enum RequestDatasetListenerWrpper {
    Dataset(RequestDatasetListenerArc),
    JavaScriptDataset(RequestJavaScriptDatasetListenerArc),
}

pub type RequestDatasetListenerArc = Arc<dyn RequestDatasetListener>;

pub trait RequestDatasetListener: Any + 'static + Send + Sync {
    fn on_receive_data(&self, _url: &str, _data: &str) {}

    fn on_receive_error(&self, _url: &str, _error: &str) {}
}

pub type RequestJavaScriptDatasetListenerArc = Arc<dyn RequestJavaScriptDatasetListener>;

pub trait RequestJavaScriptDatasetListener:
    RequestDatasetListener + Any + 'static + Send + Sync
{
    fn on_page_started(&self, _url: &str) {}

    fn on_page_cancelled(&self, _url: &str) {}

    fn on_page_finished(&self, _url: &str, _document: &str) {}

    fn on_page_error(&self, _url: &str, _error: &str) {}

    fn on_load_progress(&self, _url: &str, _progress: i32) {}

    fn on_should_override_url_loading(&self, _url: &str) {}

    fn on_should_intercept_request(&self, _url: &str) {}
}
