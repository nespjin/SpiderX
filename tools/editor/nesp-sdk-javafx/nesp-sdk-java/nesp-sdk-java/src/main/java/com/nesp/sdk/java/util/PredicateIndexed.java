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
 * This interface is used to affirm some conditions.
 * <p>
 * Team: NESP Technology
 *
 * @param <E> type elements of collection.
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Time: Created 2020/9/26 23:48
 * Project: nesp-sdk-java
 **/
public interface PredicateIndexed<E> {

    /**
     * Takes the index of an element and the element itself
     *
     * @param index of element in collection.
     * @param element of collection.
     * @return true if predicate success else false if predicate failed.
     */
    public boolean predicateIndexed(int index, E element);
}
