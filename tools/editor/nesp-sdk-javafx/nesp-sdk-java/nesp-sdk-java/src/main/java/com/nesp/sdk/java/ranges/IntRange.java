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
 * A range of values of type `Integer`.
 * <p>
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * Time: Created 2020/9/27 0:15
 * Project: nesp-sdk-java
 **/
public class IntRange extends ClosedRange<Integer> {

    /**
     * @param start        begin of range.
     * @param endInclusive end of range.
     * @param step         must not be zero.
     */
    public IntRange(Integer start, Integer endInclusive, Integer step) {
        super(start, endInclusive, step);

        if (step == 0) {
            throw new IllegalArgumentException("Step must not be zero");
        }
    }

}
