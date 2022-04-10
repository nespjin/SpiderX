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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 12/14/21 1:15 PM
 * Description:
 * GsonSerializer is a {@link JsonSerializer} witch powered by {@link com.google.gson.Gson}
 **/
public final class GsonSerializer extends JsonSerializer {

    private volatile static GsonSerializer singleton;
    private final Gson mGson;

    public static GsonSerializer shared() {
        if (singleton == null) {
            synchronized (GsonSerializer.class) {
                if (singleton == null) {
                    singleton = new GsonSerializer();
                }
            }
        }
        return singleton;
    }

    private GsonSerializer() {
        mGson = new Gson();
    }

    @Override
    public <T> String serialize(final T value) {
        return mGson.toJson(value);
    }

    @Override
    public <T> String serializePretty(final T value) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(value);
    }

    @Override
    public <T> T deserialize(final String value, final Class<T> clazz) {
        return mGson.fromJson(value, clazz);
    }

    @Override
    public <T> T deserialize(final String value, final Type type) {
        return mGson.fromJson(value, type);
    }

}
