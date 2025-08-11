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

use std::collections::HashMap;

use crate::data_source::dataset_data_source::DatasetDataSource;

pub struct DslDatasetDataSource<'local> {
    id: &'local str,
    url: &'local str,
    dsl:&'local HashMap<String, String>,
}

impl<'local> DslDatasetDataSource<'local> {
    pub fn new(id:&'local str, url:&'local str, dsl: &'local HashMap<String, String>) -> Self {
        Self { id, url, dsl }
    }
}

impl<'local> DatasetDataSource for DslDatasetDataSource<'local> {
    fn request(&self) -> Result<String, String> {
        todo!()
    }
}
