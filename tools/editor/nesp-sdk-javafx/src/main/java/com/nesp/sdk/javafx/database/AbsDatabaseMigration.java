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

/**
 * Author: <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/25 下午1:14
 * Description:
 **/
public abstract class AbsDatabaseMigration implements DatabaseMigration {

    private final int oldVersion;
    private final int newVersion;

    public AbsDatabaseMigration(int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            throw new IllegalArgumentException("old version must less than new version.");
        }
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }

    @Override
    public int getOldVersion() {
        return oldVersion;
    }

    @Override
    public int getNewVersion() {
        return newVersion;
    }

    @Override
    public boolean isAvailable(int oldVersion, int newVersion) {
        return this.oldVersion >= oldVersion && this.oldVersion < newVersion
                && this.newVersion > oldVersion && this.newVersion <= newVersion;
    }

}
