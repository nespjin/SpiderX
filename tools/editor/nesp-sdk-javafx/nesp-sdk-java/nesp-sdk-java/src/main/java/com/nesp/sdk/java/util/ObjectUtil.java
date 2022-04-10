/*
 * Copyright (c) 2020-2022.  NESP Technology.
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
