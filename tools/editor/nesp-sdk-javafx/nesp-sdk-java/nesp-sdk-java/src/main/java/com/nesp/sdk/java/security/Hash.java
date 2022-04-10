/*
 * Copyright (c) 2020-2022.  NESP Technology.
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

package com.nesp.sdk.java.security;

import com.nesp.sdk.java.text.TextUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Team: NESP Technology
 *
 * @author 靳兆鲁
 * Email: 1756404649@qq.com
 * Time: Created 18-6-21 下午4:01
 **/
public final class Hash {

    /**
     * The 16bit md5
     */
    public static final byte MD5_TYPE_SHORT = 0x00;

    /**
     * The 32bit md5
     */
    public static final byte MD5_TYPE_LONG = 0x01;

    /**
     * md5算法，默认是32位加密,加密字符串
     *
     * @param source 要计算的内容
     * @return md5 or null
     */
    public static String md5(String source) {
        if (TextUtil.isEmpty(source)) return null;
        final byte[] hashByteArray = getHashByteArray(source, "MD5");
        if (hashByteArray == null) return null;
        return byteArrayToHex(hashByteArray).toLowerCase();
    }

    /**
     * md5算法，自主选择16位或者32位加密，加密字符串
     *
     * @param source 要加密的内容
     * @param type   {@link #MD5_TYPE_SHORT} or {@link #MD5_TYPE_LONG}
     * @return md5 or null
     */
    public static String md5(String source, byte type) {
        if (TextUtil.isEmpty(source)) return null;
        final String result = md5(source);
        if (result == null) return null;
        if (type == MD5_TYPE_LONG) {
            return result;
        } else if (type == MD5_TYPE_SHORT) {
            if (result.length() < 24) return null;
            return (result.substring(8, 24));
        } else {
            return null;
        }
    }

    /**
     * 计算文件的md5值
     *
     * @param file 要计算的文件
     * @return md5 or null
     */
    public static String md5(File file) {
        if (file == null || !file.exists()) return null;
        final MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            final byte[] buffer = new byte[1024 * 8];
            int readLen;
            while ((readLen = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, readLen);
            }
            return byteArrayToHex(md5.digest()).toLowerCase();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * SHA加密
     *
     * @param source 要加密的内容
     * @return sha or null
     */
    public static String sha(String source) {
        final byte[] hashByteArray = getHashByteArray(source, "SHA");
        if (hashByteArray == null) return null;
        return byteArrayToHex(hashByteArray).toLowerCase();
    }

    /**
     * SHA加密
     *
     * @param strSrc 明文
     * @return 加密之后的密文
     */
    public static String sha256(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);

            strDes = byteArrayToHex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String hMacSha256(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = byteArrayToHex(bytes);
        } catch (Exception ignored) {

        }
        return hash;
    }

    /**
     * 获取digest byte
     *
     * @param source    要加密的内容
     * @param algorithm 加密方式
     * @return 加密的结果 or null
     */
    private static byte[] getHashByteArray(String source, String algorithm) {
        if (TextUtil.exitsEmpty(source, algorithm)) return null;
        MessageDigest mDigest;
        try {
            mDigest = MessageDigest.getInstance(algorithm);
            return mDigest.digest(source.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 下面这个函数用于将字节数组换成成16进制的字符串
    private static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    /**
     * 判断字符串是不是MD5
     *
     * @param content 要判断的内容
     * @return 是不是MD5
     */
    public static boolean isMd5(String content) {
        int cnt = 0;
        for (int i = 0; i < content.length(); ++i) {
            switch (content.charAt(i)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F': {
                    ++cnt;
                    if (32 <= cnt) return true;
                    break;
                }
                case '/': {
                    if ((i + 10) < content.length()) {// "/storage/"
                        char ch1 = content.charAt(i + 1);
                        char ch2 = content.charAt(i + 8);
                        if ('/' == ch2 && ('s' == ch1 || 'S' == ch1)) return true;
                    }
                }
                default: {
                    cnt = 0;
                    break;
                }
            }
        }
        return false;
    }
}
