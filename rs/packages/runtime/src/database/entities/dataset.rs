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

use diesel::prelude::*;

#[derive(Insertable, Queryable, Selectable, AsChangeset, Debug, Clone)]
#[diesel(table_name = crate::database::schema::dataset)]
#[diesel(check_for_backend(diesel::sqlite::Sqlite))]
pub struct DatasetEntity {
    pub id: String,
    pub plugin_id: String,
    pub url: String,
    pub url_compact: Option<String>,
    pub url_medium: Option<String>,
    pub url_expanded: Option<String>,
    pub js: Option<String>,
    pub js_compact: Option<String>,
    pub js_medium: Option<String>,
    pub js_expanded: Option<String>,
    pub dsl_default: Option<String>,
    pub dsl_compact: Option<String>,
    pub dsl_medium: Option<String>,
    pub dsl_expanded: Option<String>,
}
