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
    r2d2::{ConnectionManager, Pool, PooledConnection},
    sqlite::SqliteConnection,
};
use std::sync::{Arc, OnceLock};

use crate::database::database;

pub type SqliteConnectionManager = ConnectionManager<SqliteConnection>;
pub type SqlitePool = Pool<SqliteConnectionManager>;

/// Database connection pool manager
pub struct DatabasePool {
    pool: Arc<SqlitePool>,
}

impl DatabasePool {
    /// Get the singleton instance
    pub fn get_instance() -> &'static OnceLock<Arc<DatabasePool>> {
        static INSTANCE: OnceLock<Arc<DatabasePool>> = OnceLock::new();
        &INSTANCE
    }

    /// Initialize the connection pool
    pub fn init(database_path: &str) -> Result<Arc<DatabasePool>, String> {
        let instance = Self::get_instance();

        if let Some(existing) = instance.get() {
            return Ok(existing.clone());
        }

        // Create connection manager
        let path = format!("sqlite://{}?mode=rwc", database_path);
        let manager = SqliteConnectionManager::new(path);

        // Build connection pool with optimized settings
        let pool = Pool::builder()
            .max_size(10) // Maximum 10 connections
            .min_idle(Some(2)) // Keep at least 2 idle connections
            .connection_timeout(std::time::Duration::from_secs(30))
            .idle_timeout(Some(std::time::Duration::from_secs(300))) // 5 minutes
            .max_lifetime(Some(std::time::Duration::from_secs(1800))) // 30 minutes
            .test_on_check_out(true) // Test connections before use
            .build(manager)
            .map_err(|e| format!("Failed to create connection pool: {}", e))?;

        let db_pool = Arc::new(DatabasePool {
            pool: Arc::new(pool),
        });

        instance
            .set(db_pool.clone())
            .map_err(|_| "Database pool already initialized".to_string())?;

        log::info!("Database connection pool initialized successfully");
        Ok(db_pool)
    }

    /// Get a connection from the pool
    pub fn get_connection(&self) -> Result<PooledConnection<SqliteConnectionManager>, String> {
        self.pool
            .get()
            .map_err(|e| format!("Failed to get connection from pool: {}", e))
    }

    /// Get the pool reference
    pub fn pool(&self) -> &SqlitePool {
        &self.pool
    }

    /// Run migrations on a pooled connection
    pub fn run_migrations(&self) -> Result<(), String> {
        let mut conn = self.get_connection()?;
        // PooledConnection derefs to SqliteConnection
        database::run_migrations(&mut *conn).map_err(|e| format!("Migration failed: {}", e))?;
        log::info!("Database migrations completed successfully");
        Ok(())
    }
}

/// Helper function to get a pooled connection  
pub fn get_pooled_connection() -> Result<PooledConnection<SqliteConnectionManager>, String> {
    let pool = DatabasePool::get_instance()
        .get()
        .ok_or("Database pool not initialized")?;
    pool.get_connection()
}
