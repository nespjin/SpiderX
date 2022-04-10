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

package com.nesp.sdk.java.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/13 下午12:31
 * Description:
 **/
public interface Cache {

    void putBoolean(String key, boolean value);

    void putByte(String key, byte value);

    void putShort(String key, short value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putFloat(String key, float value);

    void putDouble(String key, double value);

    void putString(String key, String value);

    void putStringSet(String key, Set<String> value);

    Boolean getBoolean(String key);

    Byte getByte(String key);

    Short getShort(String key);

    Integer getInt(String key);

    Long getLong(String key);

    Float getFloat(String key);

    Double getDouble(String key);

    String getString(String key);

    Set<String> getStringSet(String key);

    Map<String, Object> getAll();

    boolean remove(String key);

    boolean contains(String key);

    Set<String> keys();

    List<Object> values();
}
