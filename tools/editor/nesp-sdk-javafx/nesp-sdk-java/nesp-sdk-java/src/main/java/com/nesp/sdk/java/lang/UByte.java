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
