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

package com.nesp.sdk.java.ranges;

/**
 * Represents a range of values (for example, numbers or characters).
 * <p>
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Time: Created 2020/9/27 0:17
 * Project: nesp-sdk-java
 **/
public class ClosedRange<T extends Comparable<T>> {

    /**
     * The minimum value in the range.
     */
    public T start;

    /**
     * The maximum value in the range (inclusive).
     */
    public T endInclusive;

    public T step;

    public ClosedRange(T start, T endInclusive, T step) {
        this.start = start;
        this.endInclusive = endInclusive;
        this.step = step;
    }

    /**
     * Checks whether the specified value belongs to the range.
     * @param value the specified value
     * @return true the specified value belongs to the range or false otherwise.
     */
    public boolean contains(T value) {
        return value.compareTo(start) >= 0 && value.compareTo(endInclusive) <= 0;
    }

    /**
     * Checks whether the range is empty.
     * @return true if empty or false otherwise.
     */
    public boolean isEmpty() {
        return start.compareTo(endInclusive) > 0;
    }

}
