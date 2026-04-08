// Copyright (c) 2026. NESP Technology Corporation.
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

use crate::{
    constants::DEFAULT_REQUEST_TIMEOUT,
    executor::{
        request_dataset_listener::RequestDatasetListenerWrpper,
        request_javascript_dataset_config::RequestJavaScriptDatasetConfigArc,
    },
    plugin_manager::RequestType,
};

pub struct RequestDatasetOptions {
    pub timeout: u16,
    pub req_type: RequestType,
    pub url: Option<String>,
    pub listener: Option<RequestDatasetListenerWrpper>,
    pub config: Option<RequestJavaScriptDatasetConfigArc>,
}

impl RequestDatasetOptions {
    pub fn new() -> Self {
        Self {
            timeout: DEFAULT_REQUEST_TIMEOUT,
            req_type: RequestType::Auto,
            url: None,
            listener: None,
            config: None,
        }
    }

    pub fn with_timeout(&mut self, timeout: u16) -> &mut Self {
        self.timeout = timeout;
        self
    }

    pub fn with_type(&mut self, r#type: RequestType) -> &mut Self {
        self.req_type = r#type;
        self
    }

    pub fn with_opt_url(&mut self, url: Option<String>) -> &mut Self {
        self.url = url;
        self
    }

    pub fn req_type(&self) -> RequestType {
        self.req_type
    }

    pub fn with_opt_listener(
        &mut self,
        listener: Option<RequestDatasetListenerWrpper>,
    ) -> &mut Self {
        self.listener = listener;
        self
    }

    pub fn with_listener(&mut self, listener: RequestDatasetListenerWrpper) -> &mut Self {
        self.listener.replace(listener);
        self
    }

    pub fn listener(&self) -> &Option<RequestDatasetListenerWrpper> {
        &self.listener
    }

    pub fn with_opt_config(
        &mut self,
        config: Option<RequestJavaScriptDatasetConfigArc>,
    ) -> &mut Self {
        self.config = config;
        self
    }

    pub fn with_config(&mut self, config: RequestJavaScriptDatasetConfigArc) -> &mut Self {
        self.config.replace(config);
        self
    }

    pub fn config(&self) -> &Option<RequestJavaScriptDatasetConfigArc> {
        &self.config
    }
}
