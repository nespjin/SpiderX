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

import java.lang.reflect.Type;

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 12/14/21 1:03 PM
 * Description:
 * Serializer can serialize the origin data to serialized data,
 * and deserialize serialized data to origin data.
 *
 * @param <SerializedType> The data type of serialized data
 **/
public interface Serializer<SerializedType> {

    /**
     * Serialize the origin data to serialized data
     *
     * @param value the origin data
     * @param <T>   the data type of origin data
     * @return serialized data
     */
    <T> SerializedType serialize(T value);

    /**
     * @param value the serialized data
     * @param clazz the class of origin data
     * @param <T>   the data type of origin data
     * @return origin data
     */
    <T> T deserialize(SerializedType value, Class<T> clazz);

    /**
     * @param value the serialized data
     * @param type  the type of origin data
     * @param <T>   the data type of origin data
     * @return origin data
     */
    <T> T deserialize(SerializedType value, Type type);


}
