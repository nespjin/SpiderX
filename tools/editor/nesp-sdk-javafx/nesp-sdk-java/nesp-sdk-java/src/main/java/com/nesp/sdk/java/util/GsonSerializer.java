/*
 *
 *   Copyright (c) 2021  NESP Technology Corporation. All rights reserved.
 *
 *   This program is not free software; you can't redistribute it and/or modify it
 *   without the permit of team manager.
 *
 *   Unless required by applicable law or agreed to in writing.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
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
