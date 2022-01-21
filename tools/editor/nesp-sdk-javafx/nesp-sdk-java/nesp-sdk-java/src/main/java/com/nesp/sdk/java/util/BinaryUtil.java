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
