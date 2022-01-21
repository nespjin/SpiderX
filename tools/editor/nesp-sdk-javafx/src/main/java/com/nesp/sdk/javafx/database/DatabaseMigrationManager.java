/*
 * Copyright 2021 NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.javafx.database;


import com.nesp.sdk.java.util.Log;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Author: <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/25 下午1:22
 * Description:
 * <p>
 * Usage:
 * <code>
 * public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
 * final DatabaseMigrationManager databaseMigrationManager = new DatabaseMigrationManager(database, oldVersion, newVersion);
 * databaseMigrationManager.setOnMigrationListener(new DatabaseMigrationManager.OnMigrationListener() {
 *
 * @Override public void onBeforeMigration() {
 * }
 * @Override public void onMigration(final AbsDatabaseMigration migration) {
 * }
 * @Override public void onAfterMigration() {
 * }
 * });
 * <p>
 * databaseMigrationManager.addMigration(new Migration_1_2());
 * databaseMigrationManager.addMigration(new Migration_2_3());
 * databaseMigrationManager.addMigration(new Migration_3_4());
 * databaseMigrationManager.addMigration(new Migration_4_5());
 * <p>
 * <p>
 * databaseMigrationManager.migrate();
 * <p>
 * }
 * </code>
 **/
public class DatabaseMigrationManager {

    private static final String TAG = "DatabaseMigrationManager";

    private final List<DatabaseMigration> mDatabaseMigrations = new ArrayList<>();

    private final int mOldVersion;
    private final int mNewVersion;
    private final Database mDatabase;

    private OnMigrationListener mOnMigrationListener;

    public DatabaseMigrationManager(Database database, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            throw new IllegalArgumentException("old version must less than new version");
        }
        this.mDatabase = database;
        this.mOldVersion = oldVersion;
        this.mNewVersion = newVersion;
    }

    public DatabaseMigrationManager addMigration(DatabaseMigration databaseMigration) {
        this.mDatabaseMigrations.add(databaseMigration);
        return this;
    }

    public DatabaseMigrationManager addMigrations(ArrayList<DatabaseMigration> databaseMigrations) {
        this.mDatabaseMigrations.addAll(databaseMigrations);
        return this;
    }

    public DatabaseMigrationManager setOnMigrationListener(final OnMigrationListener onMigrationListener) {
        this.mOnMigrationListener = onMigrationListener;
        return this;
    }

    public void migrate() throws SQLException {
        Log.d(TAG, "migration: old version = " + mOldVersion);
        Log.d(TAG, "migration: new version = " + mNewVersion);
        if (mOnMigrationListener != null) {
            mOnMigrationListener.onBeforeMigration();
        }
        final List<DatabaseMigration> availableMigrations = getAvailableMigrations();
        sort(availableMigrations);

        final Statement statement = mDatabase.beginTransaction();
        ArrayList<String> migrationSqlCommands = new ArrayList<>();
        for (final DatabaseMigration migration : availableMigrations) {
            Log.d(TAG, "migrate: available migration = " + migration.getClass().getSimpleName());
            if (mOnMigrationListener != null) {
                mOnMigrationListener.onMigration(migration);
            }
            migration.migrationSqlCommands(migrationSqlCommands);
        }

        for (final String migrationSqlCommand : migrationSqlCommands) {
            Log.d(TAG, "migrate: migration sql commands = " + migrationSqlCommand);
            statement.execute(migrationSqlCommand);
        }

        statement.closeOnCompletion();
        if (mOnMigrationListener != null) {
            mOnMigrationListener.onAfterMigration();
        }
    }

    private List<DatabaseMigration> getAvailableMigrations() {
        List<DatabaseMigration> migrations = new ArrayList<>();
        for (final DatabaseMigration databaseMigration : mDatabaseMigrations) {
            if (databaseMigration.isAvailable(mOldVersion, mNewVersion)) {
                migrations.add(databaseMigration);
            }
        }
        return migrations;
    }

    private void sort(List<DatabaseMigration> migrations) {
        migrations.sort(Comparator.comparingInt(DatabaseMigration::getOldVersion));
    }

    public interface OnMigrationListener {

        void onBeforeMigration();

        void onMigration(DatabaseMigration migration);

        void onAfterMigration();

    }

}
