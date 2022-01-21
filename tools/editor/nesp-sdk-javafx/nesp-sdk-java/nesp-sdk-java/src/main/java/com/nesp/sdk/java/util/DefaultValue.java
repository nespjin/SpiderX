/*
 * Copyright (C) 2021 The NESP Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.util;

/**
 * This class is used to provide default value for element of collection with function {@code
 * defaultValue}.
 * <p>
 * Team: NESP Technology
 *
 * @param <E> type of default value
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Time: Created 2020/9/26 21:10
 * Project: nesp-sdk-java
 **/
public interface DefaultValue<E> {

    /**
     * @param index of element at collection.
     * @return default value
     */
    public E defaultValue(int index);
}
