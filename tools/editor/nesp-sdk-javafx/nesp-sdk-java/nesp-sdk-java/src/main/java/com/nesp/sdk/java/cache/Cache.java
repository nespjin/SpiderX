package com.nesp.sdk.java.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/13 下午12:31
 * Description:
 **/
public interface Cache {

    void putBoolean(String key, boolean value);

    void putByte(String key, byte value);

    void putShort(String key, short value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putFloat(String key, float value);

    void putDouble(String key, double value);

    void putString(String key, String value);

    void putStringSet(String key, Set<String> value);

    Boolean getBoolean(String key);

    Byte getByte(String key);

    Short getShort(String key);

    Integer getInt(String key);

    Long getLong(String key);

    Float getFloat(String key);

    Double getDouble(String key);

    String getString(String key);

    Set<String> getStringSet(String key);

    Map<String, Object> getAll();

    boolean remove(String key);

    boolean contains(String key);

    Set<String> keys();

    List<Object> values();
}
