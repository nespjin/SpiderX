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

package com.nesp.sdk.javafx.content;

import com.nesp.sdk.java.util.Log;

import java.util.*;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/29 下午9:30
 * Description:
 **/
public final class ContentValues {

    private static final String TAG = "ContentValues";

    private final HashMap<String, Object> mMap;

    public ContentValues() {
        mMap = new HashMap<>();
    }

    public ContentValues(int size) {
        mMap = new HashMap<>(size);
    }

    public ContentValues(ContentValues from) {
        Objects.requireNonNull(from);
        mMap = new HashMap<>(from.mMap);
    }

    public ContentValues(HashMap<String, Object> from) {
        Objects.requireNonNull(from);
        mMap = new HashMap<>();
        mMap.putAll(from);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ContentValues)) {
            return false;
        }
        return mMap.equals(((ContentValues) o).mMap);
    }

    public HashMap<String, Object> getValues() {
        return mMap;
    }

    @Override
    public int hashCode() {
        return mMap.hashCode();
    }

    public void put(String key, String value) {
        mMap.put(key, value);
    }

    public void putAll(ContentValues other) {
        mMap.putAll(other.mMap);
    }

    public void put(String key, Byte value) {
        mMap.put(key, value);
    }

    public void put(String key, Short value) {
        mMap.put(key, value);
    }

    public void put(String key, Integer value) {
        mMap.put(key, value);
    }

    public void put(String key, Long value) {
        mMap.put(key, value);
    }

    public void put(String key, Float value) {
        mMap.put(key, value);
    }

    public void put(String key, Double value) {
        mMap.put(key, value);
    }

    public void put(String key, Boolean value) {
        mMap.put(key, value);
    }

    public void put(String key, byte[] value) {
        mMap.put(key, value);
    }

    public void putNull(String key) {
        mMap.put(key, null);
    }

    public void putObject(String key, Object value) {
        if (value == null) {
            putNull(key);
        } else if (value instanceof String) {
            put(key, (String) value);
        } else if (value instanceof Byte) {
            put(key, (Byte) value);
        } else if (value instanceof Short) {
            put(key, (Short) value);
        } else if (value instanceof Integer) {
            put(key, (Integer) value);
        } else if (value instanceof Long) {
            put(key, (Long) value);
        } else if (value instanceof Float) {
            put(key, (Float) value);
        } else if (value instanceof Double) {
            put(key, (Double) value);
        } else if (value instanceof Boolean) {
            put(key, (Boolean) value);
        } else if (value instanceof byte[]) {
            put(key, (byte[]) value);
        } else {
            throw new IllegalArgumentException("Unsupported type " + value.getClass());
        }
    }

    public int size() {
        return mMap.size();
    }

    public boolean isEmpty() {
        return mMap.isEmpty();
    }

    public void remove(String key) {
        mMap.remove(key);
    }

    public void clear() {
        mMap.clear();
    }

    public boolean containsKey(String key) {
        return mMap.containsKey(key);
    }

    public Object get(String key) {
        return mMap.get(key);
    }

    public String getAsString(String key) {
        final Object value = mMap.get(key);
        return value != null ? value.toString() : null;
    }

    public Long getAsLong(String key) {
        final Object value = mMap.get(key);
        try {
            return value != null ? ((Number) value).longValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Long.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG,
                            "getAsLong: Cannot parse Long value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG,
                        "getAsLong: Cannot case value for " + key + " to a Long: " + value + "\n" +
                                e.getLocalizedMessage());
                return null;
            }
        }
    }

    public Integer getAsInteger(String key) {
        final Object value = mMap.get(key);
        try {
            return value != null ? ((Number) value).intValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Integer.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG,
                            "getAsInteger: Cannot parse Long value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG,
                        "getAsInteger: Cannot case value for " + key + " to a Integer: " + value + "\n" +
                                e.getLocalizedMessage());
                return null;
            }
        }
    }

    public Short getAsShort(String key) {
        final Object value = mMap.get(key);
        try {
            return value != null ? ((Number) value).shortValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Short.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG,
                            "getAsShort: Cannot parse Short value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG,
                        "getAsShort: Cannot case value for " + key + " to a Short: " + value + "\n" +
                                e.getLocalizedMessage());
                return null;
            }
        }
    }

    public Byte getAsByte(String key) {
        final Object value = mMap.get(key);
        try {
            return value != null ? ((Number) value).byteValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Byte.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG,
                            "getAsByte: Cannot parse Byte value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG,
                        "getAsByte: Cannot case value for " + key + " to a Byte: " + value + "\n" +
                                e.getLocalizedMessage());
                return null;
            }
        }
    }

    public Double getAsDouble(String key) {
        final Object value = mMap.get(key);
        try {
            return value != null ? ((Number) value).doubleValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Double.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG,
                            "getAsDouble: Cannot parse Double value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG,
                        "getAsDouble: Cannot case value for " + key + " to a Double: " + value + "\n" +
                                e.getLocalizedMessage());
                return null;
            }
        }
    }

    public Float getAsFloat(String key) {
        final Object value = mMap.get(key);
        try {
            return value != null ? ((Number) value).floatValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Float.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG,
                            "getAsFloat: Cannot parse Float value for " + value + " at key " + key);
                    return null;
                }
            } else {
                Log.e(TAG,
                        "getAsFloat: Cannot case value for " + key + " to a Float: " + value + "\n" +
                                e.getLocalizedMessage());
                return null;
            }
        }
    }

    public Boolean getAsBoolean(String key) {
        final Object value = mMap.get(key);
        try {
            return (Boolean) value;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                return Boolean.parseBoolean(value.toString()) || "1".equals(value);
            } else if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            } else {
                Log.e(TAG,
                        "getAsBoolean: Cannot case value for " + key + " to a Boolean: " + value +
                                "\n" + e.getLocalizedMessage());
                return null;
            }
        }
    }

    public byte[] getAsByteArray(String key) {
        final Object value = mMap.get(key);
        if (value instanceof byte[]) {
            return (byte[]) value;
        } else {
            return null;
        }
    }

    public Set<Map.Entry<String, Object>> valueSet() {
        return mMap.entrySet();
    }

    public Set<String> keySet() {
        return mMap.keySet();
    }

    public void putStringArrayList(String key, ArrayList<String> value) {
        mMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getStringArrayList(String key) {
        return (ArrayList<String>) mMap.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : mMap.keySet()) {
            String value = getAsString(name);
            if (sb.length() > 0) sb.append(" ");
            sb.append(name).append("=").append(value);
        }
        return sb.toString();
    }

    public static boolean isSupportedValue(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String) {
            return true;
        } else if (value instanceof Byte) {
            return true;
        } else if (value instanceof Short) {
            return true;
        } else if (value instanceof Integer) {
            return true;
        } else if (value instanceof Long) {
            return true;
        } else if (value instanceof Float) {
            return true;
        } else if (value instanceof Double) {
            return true;
        } else if (value instanceof Boolean) {
            return true;
        } else if (value instanceof byte[]) {
            return true;
        } else {
            return false;
        }
    }
}
