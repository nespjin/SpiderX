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

use crate::database::{entities::dataset::DatasetEntity, schema::dataset};

pub fn upsert(conn: &mut SqliteConnection, entity: &DatasetEntity) -> QueryResult<DatasetEntity> {
    diesel::insert_or_ignore_into(dataset::table)
        .values(entity)
        .on_conflict(dataset::id)
        .do_update()
        .set(entity)
        .returning(DatasetEntity::as_returning())
        .get_result(conn)
}

pub fn upsert_all(conn: &mut SqliteConnection, entities: &[DatasetEntity]) -> QueryResult<usize> {
    conn.transaction(|conn: &mut SqliteConnection| {
        let mut count: usize = 0;
        for entity in entities {
            diesel::insert_or_ignore_into(dataset::table)
                .values(entity)
                .on_conflict(dataset::id)
                .do_update()
                .set(entity)
                .returning(DatasetEntity::as_returning())
                .get_result(conn)?;
            count += 1;
        }
        Ok(count)
    })
}

pub fn insert_all(conn: &mut SqliteConnection, entities: &[DatasetEntity]) -> QueryResult<usize> {
    diesel::insert_or_ignore_into(dataset::table)
        .values(entities)
        .execute(conn)
}

pub fn update_all(conn: &mut SqliteConnection, entities: &[DatasetEntity]) -> QueryResult<usize> {
    conn.transaction(|conn: &mut SqliteConnection| {
        let mut count: usize = 0;
        for entity in entities {
            diesel::update(dataset::table.filter(dataset::id.eq(&entity.id)))
                .set(entity)
                .execute(conn)?;
            count += 1;
        }
        Ok(count)
    })
}

pub fn update_by_id(
    conn: &mut SqliteConnection,
    id: &str,
    entity: &DatasetEntity,
) -> QueryResult<usize> {
    diesel::update(dataset::table.filter(dataset::id.eq(id)))
        .set(entity)
        .execute(conn)
}

pub fn find_by_id(conn: &mut SqliteConnection, id: &str) -> QueryResult<Option<DatasetEntity>> {
    match dataset::table.filter(dataset::id.eq(id)).first(conn) {
        Ok(dataset) => Ok(Some(dataset)),
        Err(diesel::NotFound) => Ok(None),
        Err(err) => Err(err),
    }
}

pub fn find_by_plugin_id(
    conn: &mut SqliteConnection,
    plugin_id: &str,
) -> QueryResult<Vec<DatasetEntity>> {
    dataset::table
        .filter(dataset::plugin_id.eq(plugin_id))
        .load(conn)
}

pub fn find_by_id_in_plugin(
    conn: &mut SqliteConnection,
    plugin_id: &str,
    id: &str,
) -> QueryResult<Option<DatasetEntity>> {
    match dataset::table
        .filter(dataset::plugin_id.eq(plugin_id))
        .filter(dataset::id.eq(id))
        .first(conn)
    {
        Ok(dataset) => Ok(Some(dataset)),
        Err(diesel::NotFound) => Ok(None),
        Err(err) => Err(err),
    }
}

pub fn is_dataset_exists(conn: &mut SqliteConnection, id: &str) -> QueryResult<bool> {
    dataset::table
        .filter(dataset::id.eq(id))
        .select(dataset::id)
        .first::<String>(conn)
        .map(|_| true)
        .or_else(|_| Ok(false))
}

pub fn find_all(conn: &mut SqliteConnection) -> QueryResult<Vec<DatasetEntity>> {
    dataset::table.load(conn)
}

pub fn delete_by_plugin_id(conn: &mut SqliteConnection, plugin_id: &str) -> QueryResult<usize> {
    diesel::delete(dataset::table.filter(dataset::plugin_id.eq(plugin_id))).execute(conn)
}

pub fn delete_by_id(conn: &mut SqliteConnection, id: &str) -> QueryResult<usize> {
    diesel::delete(dataset::table.filter(dataset::id.eq(id))).execute(conn)
}

pub fn delete_all(conn: &mut SqliteConnection) -> QueryResult<usize> {
    diesel::delete(dataset::table).execute(conn)
}
