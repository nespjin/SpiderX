/*
 *
 *   Copyright (c) 2020  NESP Technology Corporation. All rights reserved.
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

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 18-12-17 下午2:51
 **/
@Deprecated
public class ObjectUtil {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    @Deprecated
    public static <T> String modelToString(T t) {
        StringBuilder result = new StringBuilder("[");
        for (Field declaredField : t.getClass().getDeclaredFields()) {
            try {
                result.append(declaredField.getName())
                        .append("=")
                        .append(declaredField.get(t))
                        .append(",");
            } catch (IllegalAccessException e) {
                declaredField.setAccessible(true);
                try {
                    result
                            .append(declaredField.get(t))
                            .append(",");
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                declaredField.setAccessible(false);
            }
        }

        return result.substring(0, result.length() - 1) + "]";
    }

    @Deprecated
    public static <T> String objToJson(Class<T> classOfT) {
        return new Gson().toJson(classOfT);
    }

    @Deprecated
    public static <T> String objToJsonByGson(Gson gson, Class<T> classOfT) {
        return gson.toJson(classOfT);
    }

    @Deprecated
    public static <T> String objToJsonByFastJson(Gson gson, Class<T> classOfT) {
        return JSON.toJSONString(classOfT);
    }

    @Deprecated
    public static <T> T jsonToObj(String json, Class<T> classOfT) {
        return new Gson().fromJson(json, classOfT);
    }

    @Deprecated
    public static <T> T jsonToObjByGson(Gson gson, String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    @Deprecated
    public static <T> T jsonToObjByFastJson(Gson gson, String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

}
