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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/17 上午11:08
 *
 * @deprecated Use class within {@link com.nesp.sdk.java.security} package
 **/
@Deprecated
public final class SecurityUtil {
    private SecurityUtil() {
    }

    public static String md5(String text) {
        return md5(text, false);
    }

    public static String md5(String text, boolean isShort) {
        try {
            final MessageDigest instance = MessageDigest.getInstance("MD5");
            final byte[] digest = instance.digest(text.getBytes(StandardCharsets.UTF_8));
            final StringBuilder md5 = new StringBuilder();
            for (final byte b : digest) {
                final int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                md5.append(hexString);
            }
            return isShort ? md5.substring(8, 24) : md5.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
