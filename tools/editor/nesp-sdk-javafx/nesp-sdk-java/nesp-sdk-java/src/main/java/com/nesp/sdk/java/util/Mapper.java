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
package com.nesp.sdk.java.util;

/**
 * This interface is used to foreach element in collection.
 * <p>
 *
 * @param <T> the type of element in collection.
 * @param <R> the type of returns.
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Date 2020-9-26 3:20:20
 */
public interface Mapper<R, T> {

    /**
     * @param element of collection.
     * @return type of conversion
     */
    public R map(T element);

}
