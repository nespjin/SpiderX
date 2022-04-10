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

package com.nesp.sdk.java.lang;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/8/20 17:57
 **/
class UByte extends Number implements Comparable<UByte> {
    private static final String TAG = "UByte";

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 255;
    public static final int SIZE = Byte.SIZE;
    public static final int BYTES = SIZE / Byte.SIZE;

    private final int value;

    public UByte(final int value) {
        this.value = value;
    }

    public UByte(final byte value) {
        this.value = value & 0xFF;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return (double) value;
    }

    @Override
    public int compareTo(final UByte o) {
        return Integer.compare(value, o.value);
    }

    public String toHexString() {
        return Integer.toHexString(value);
    }

}
