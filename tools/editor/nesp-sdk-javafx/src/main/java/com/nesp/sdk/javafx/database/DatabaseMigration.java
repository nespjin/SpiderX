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

import java.util.List;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/25 下午2:35
 * Description:
 **/
public interface DatabaseMigration {
    /**
     * @return the old version for migration.
     */
    int getOldVersion();

    /**
     * @return the new version for migration.
     */
    int getNewVersion();

    /**
     * Fills migration sql commands to migrationSqlCommands.
     *
     * @param migrationSqlCommands the sql commands to fill.
     */
    void migrationSqlCommands(List<String> migrationSqlCommands);

    /**
     * Check the migration is available for database.
     *
     * @param oldVersion the old database version.
     * @param newVersion the new database version.
     * @return is available for database.
     */
    boolean isAvailable(int oldVersion, int newVersion);
}
