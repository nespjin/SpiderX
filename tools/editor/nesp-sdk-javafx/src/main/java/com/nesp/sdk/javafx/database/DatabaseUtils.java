/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.javafx.database;

import java.util.List;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/30 10:05
 * Description:
 **/
public final class DatabaseUtils {

    private static final String TAG = "DatabaseUtils";

    private DatabaseUtils() {
    }

    public static String compileInValuesString(final List<String> values) {
        final StringBuilder valueString = new StringBuilder();
        for (final String value : values) {
            if (!valueString.isEmpty()) {
                valueString.append(",");
            }
            valueString.append("'");
            valueString.append(value);
            valueString.append("'");
        }
        return valueString.toString();
    }

    public static String compileKeywords(final String keywords) {
        String keywordsTmp = keywords.replaceAll(" ", "");
        keywordsTmp = keywordsTmp.replaceAll("'", "");
        keywordsTmp = keywordsTmp.replaceAll("%", "");
        return "%" + keywordsTmp + "%";
    }

}
