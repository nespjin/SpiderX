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
