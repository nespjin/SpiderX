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

use diesel::{
    Connection, ExpressionMethods, QueryDsl, QueryResult, RunQueryDsl, SelectableHelper,
    SqliteConnection,
};

use crate::database::{entities::plugin::PluginEntity, schema::plugin};

pub(crate) fn upsert(
    conn: &mut SqliteConnection,
    entity: &PluginEntity,
) -> QueryResult<PluginEntity> {
    diesel::insert_or_ignore_into(plugin::table)
        .values(entity)
        .on_conflict(plugin::id)
        .do_update()
        .set(entity)
        .returning(PluginEntity::as_returning())
        .get_result(conn)
}

pub(crate) fn upsert_all(
    conn: &mut SqliteConnection,
    entities: &[PluginEntity],
) -> QueryResult<usize> {
    conn.transaction(|conn: &mut SqliteConnection| {
        let mut count: usize = 0;
        for entity in entities {
            diesel::insert_or_ignore_into(plugin::table)
                .values(entity)
                .on_conflict(plugin::id)
                .do_update()
                .set(entity)
                .returning(PluginEntity::as_returning())
                .get_result(conn)?;
            count += 1;
        }
        Ok(count)
    })
}

pub(crate) fn insert_all(
    conn: &mut SqliteConnection,
    entities: &[PluginEntity],
) -> QueryResult<usize> {
    diesel::insert_or_ignore_into(plugin::table)
        .values(entities)
        .execute(conn)
}

pub(crate) fn update_all(
    conn: &mut SqliteConnection,
    entities: &[PluginEntity],
) -> QueryResult<usize> {
    conn.transaction(|conn: &mut SqliteConnection| {
        let mut count: usize = 0;
        for entity in entities {
            diesel::update(plugin::table.filter(plugin::id.eq(&entity.id)))
                .set(entity)
                .execute(conn)?;
            count += 1;
        }
        Ok(count)
    })
}

pub(crate) fn update_by_id(
    conn: &mut SqliteConnection,
    id: &str,
    entity: &PluginEntity,
) -> QueryResult<usize> {
    diesel::update(plugin::table.filter(plugin::id.eq(id)))
        .set(entity)
        .execute(conn)
}

pub(crate) fn find_by_id(
    conn: &mut SqliteConnection,
    id: &str,
) -> QueryResult<Option<PluginEntity>> {
    match plugin::table.filter(plugin::id.eq(id)).first(conn) {
        Ok(entity) => Ok(Some(entity)),
        Err(diesel::NotFound) => Ok(None),
        Err(e) => Err(e),
    }
}

pub(crate) fn find_all(conn: &mut SqliteConnection) -> QueryResult<Vec<PluginEntity>> {
    plugin::table.load(conn)
}

pub(crate) fn is_plugin_exists(conn: &mut SqliteConnection, id: &str) -> QueryResult<bool> {
    plugin::table
        .filter(plugin::id.eq(id))
        .select(plugin::id)
        .first::<String>(conn)
        .map(|_| true)
        .or_else(|_| Ok(false))
}

pub(crate) fn delete_by_id(conn: &mut SqliteConnection, id: &str) -> QueryResult<usize> {
    diesel::delete(plugin::table.filter(plugin::id.eq(id))).execute(conn)
}

pub(crate) fn delete_all(conn: &mut SqliteConnection) -> QueryResult<usize> {
    diesel::delete(plugin::table).execute(conn)
}
