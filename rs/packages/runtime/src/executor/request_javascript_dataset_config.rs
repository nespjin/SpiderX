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

use std::sync::Arc;

pub type RequestJavaScriptDatasetConfigArc = Arc<dyn RequestJavaScriptDatasetConfig>;

pub trait RequestJavaScriptDatasetConfig: Send + Sync {
    fn should_override_url_loading(&self, _url: &str) -> Option<bool> {
        None
    }

    fn should_intercept_request(&self, _url: &str) -> Option<String> {
        None
    }
}
