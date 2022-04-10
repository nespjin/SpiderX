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
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/8/20 13:28
 **/
public class BinaryUtil {
    private static final String TAG = "BinaryUtil";

    /**
     * 获取某一个数的二进制位的某一位是1还是0
     *
     * @param num   待判断的数
     * @param index 待判断的位（右往左）start with 0
     * @return
     */
    public static int getBinaryBit(int num, int index) {
        return (num >> index) & 1;
    }

//    取高八位：num >> 8;
//
//    取低八位：num & 0xff。
}
