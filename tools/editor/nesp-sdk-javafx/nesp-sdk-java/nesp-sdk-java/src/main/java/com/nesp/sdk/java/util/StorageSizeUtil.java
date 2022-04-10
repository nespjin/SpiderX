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

import java.util.Locale;

/**
 * Team: NESP Technology
 *
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * @version 1.0
 * Time: Created 2021/12/11 1:52
 * Description:
 **/
public final class StorageSizeUtil {

    private StorageSizeUtil() {
        //no instance
    }

    /**
     * @param size    大小
     * @return 格式化后的存储大小
     */
    public static String format(long size) {
        return format(size, 2);
    }

    /**
     * @param size    大小
     * @param units   单位
     * @return 格式化后的存储大小
     */
    public static String format(long size, String[] units) {
        return format(size, units, 2);
    }

    /**
     * @param size    大小
     * @param decimal 小数点后几位
     * @return 格式化后的存储大小
     */
    public static String format(long size, int decimal) {
        return format(size, new String[]{"B", "KB", "MB", "GB", "TB"}, decimal);
    }

    /**
     * @param size    大小
     * @param units   单位
     * @param decimal 小数点后几位
     * @return 格式化后的存储大小
     */
    public static String format(long size, String[] units, int decimal) {
        int index = 0;
        float result = size;
        while (result >= 1024 && index < units.length - 1) {
            result = result / 1024;
            index++;
        }
        return String.format(Locale.getDefault(), "%." + decimal + "f%s", result, units[index]);
    }

}
