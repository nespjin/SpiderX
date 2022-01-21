/*
 *
 *   Copyright (c) 2021  NESP Technology Corporation. All rights reserved.
 *
 *   This program is not free software; you can't redistribute it and/or modify it
 *   without the permit of team manager.
 *
 *   Unless required by applicable law or agreed to in writing.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
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
