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
 * Author: <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/25 下午2:50
 * Description:
 **/
public class MigrationSample extends AbsDatabaseMigration {

    public MigrationSample() {
        super(1, 2);
    }

    @Override
    public void migrationSqlCommands(final List<String> migrationSqlCommands) {
        migrationSqlCommands.add("ALTER TABLE `table` ADD `count` NUMERIC NOT NULL DEFAULT 1;");
        migrationSqlCommands.add("ALTER TABLE `table` ADD `title` TEXT NOT NULL DEFAULT '';");
    }


}
