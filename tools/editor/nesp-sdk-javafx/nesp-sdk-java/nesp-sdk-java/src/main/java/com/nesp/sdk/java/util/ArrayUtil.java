package com.nesp.sdk.java.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/8/4 15:10
 **/
public final class ArrayUtil {
    private static final String TAG = "ArrayUtils";

    private ArrayUtil() {
    }

    public static <E> boolean isEmpty(E[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static <E> boolean isEmptyOrLengthLessThan(E[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> boolean isEmptyOrLengthLessThan(byte[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> boolean isEmptyOrLengthLessThan(short[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> boolean isEmptyOrLengthLessThan(int[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> boolean isEmptyOrLengthLessThan(long[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> boolean isEmptyOrLengthLessThan(float[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> boolean isEmptyOrLengthLessThan(double[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> boolean isEmptyOrLengthLessThan(boolean[] array, int length) {
        return array == null || array.length < length;
    }

    public static <E> Collection<E> toCollection(E[] array) {
        if (isEmpty(array)) return Collections.emptyList();
        LinkedList<E> list = new LinkedList<>();
        Collections.addAll(list, array);
        return list;
    }
}
