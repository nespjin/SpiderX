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

use crate::{executor::dataset_executor::DatasetExecutor, plugin_manager::PluginManager};

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
        let webengine = webengine.read().unwrap();

        webengine.load_url(self.url)?;

        Ok("".to_string())
    }
}
