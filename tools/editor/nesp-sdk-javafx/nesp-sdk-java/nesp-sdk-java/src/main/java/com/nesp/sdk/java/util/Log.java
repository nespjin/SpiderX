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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/3 22:28
 * Description:
 **/
public final class Log {

    private Log() {
    }

    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARN = 3;
    public static final int LEVEL_ERROR = 4;


    public static void v(String tag, String msg) {
        printLog(LEVEL_VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        printLog(LEVEL_DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        printLog(LEVEL_INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        printLog(LEVEL_WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        printLog(LEVEL_ERROR, tag, msg);
    }

    /**
     * / Print log to console
     */
    public static void printLog(int level, String tag, String msg) {
        final String levelTitle = getLevelTitle(level);

        final String logDateTime =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        final String log = String.format(
                logDateTime + " %s/%s: %s",
                levelTitle, tag, msg);
        if (level == LEVEL_ERROR) {
            System.out.println("\033[31m" + log + "\033[0m");
        } else if (level == LEVEL_WARN) {
            System.out.println("\033[33m" + log + "\033[0m");
        } else {
            System.out.println(log);
        }
    }

    /**
     * / Return title is not empty.
     */
    public static String getLevelTitle(int level) {
        String title;
        switch (level) {
            case LEVEL_VERBOSE:
                title = "V";
                break;
            case LEVEL_DEBUG:
                title = "D";
                break;
            case LEVEL_INFO:
                title = "I";
                break;
            case LEVEL_WARN:
                title = "W";
                break;
            case LEVEL_ERROR:
                title = "E";
                break;
            default:
                throw new IllegalArgumentException(level + " is not matched");
        }
        return title;
    }

}
