/*
 * Copyright (c) 2021-2022.  NESP Technology.
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

package com.nesp.sdk.java.util;

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 12/14/21 1:15 PM
 * Description:
 * JsonSerializer is a {@link StringSerializer} witch to serialize for Json
 **/
public abstract class JsonSerializer extends StringSerializer {

    /**
     * Serialize the origin data to pretty json string, do not recommend to call this method.
     *
     * @param value the origin data
     * @param <T>   the data type of origin data
     * @return Json String pretty
     */
    public abstract <T> String serializePretty(final T value);

}
