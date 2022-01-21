package com.nesp.sdk.java.cache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/13 下午12:59
 * Description:
 **/
public final class StoreCache implements Cache {

    private final File mStoreFile;
    private final JSONObject mJSONObjectRoot;

    public StoreCache(String storePath) {
        this(new File(storePath));
    }

    public StoreCache(File storeFile) {
        JSONObject jsonObjectRoot = null;
        this.mStoreFile = storeFile;

        if (!mStoreFile.exists()) {
            if (mStoreFile.getParentFile() != null
                    && !mStoreFile.getParentFile().exists()) {
                if (!mStoreFile.getParentFile().mkdirs()) {
                    System.out.println("store file parent mkdirs is failed");
                }
            }
            try {
                if (!mStoreFile.createNewFile()) {
                    System.out.println("the store file create failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("the store file create failed: " + e.getLocalizedMessage());
            }
        }

        byte[] buffer = new byte[1024];
        int readLength;
        try (InputStream inputStream = new FileInputStream(mStoreFile);
             OutputStream outputStream = new ByteArrayOutputStream()) {
            while ((readLength = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readLength);
            }
            String source = outputStream.toString();
            if (source.isEmpty()) source = "{}";
            jsonObjectRoot = new JSONObject(source);
        } catch (IOException | JSONException e) {
            System.out.println("load store file failed: " + e.getLocalizedMessage());
        }

        if (jsonObjectRoot == null) jsonObjectRoot = new JSONObject("{}");
        mJSONObjectRoot = jsonObjectRoot;

    }

    public synchronized void commit() {
        try (OutputStream outputStream = new FileOutputStream(mStoreFile)) {
            outputStream.write(mJSONObjectRoot.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putBoolean(final String key, final boolean value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putByte(final String key, final byte value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putShort(final String key, final short value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putInt(final String key, final int value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putLong(final String key, final long value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putFloat(final String key, final float value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putDouble(final String key, final double value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putString(final String key, final String value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public void putStringSet(final String key, final Set<String> value) {
        mJSONObjectRoot.put(key, value);
    }

    @Override
    public Boolean getBoolean(final String key) {
        try {
            return mJSONObjectRoot.getBoolean(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Byte getByte(final String key) {
        try {
            return ((byte) mJSONObjectRoot.getInt(key));
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Short getShort(final String key) {
        try {
            return ((short) mJSONObjectRoot.getInt(key));
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Integer getInt(final String key) {
        try {
            return mJSONObjectRoot.getInt(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Long getLong(final String key) {
        try {
            return mJSONObjectRoot.getLong(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Float getFloat(final String key) {
        try {
            return mJSONObjectRoot.getFloat(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Double getDouble(final String key) {
        try {
            return mJSONObjectRoot.getDouble(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String getString(final String key) {
        try {
            return mJSONObjectRoot.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Set<String> getStringSet(final String key) {
        try {
            final JSONArray array = mJSONObjectRoot.getJSONArray(key);
            final Set<String> stringSet = new HashSet<>();
            for (int i = 0; i < array.length(); i++) {
                stringSet.add(array.getString(i));
            }
            return stringSet;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> getAll() {
        return mJSONObjectRoot.toMap();
    }

    @Override
    public boolean remove(final String key) {
        return mJSONObjectRoot.remove(key) != null;
    }

    @Override
    public boolean contains(final String key) {
        return mJSONObjectRoot.has(key);
    }

    @Override
    public Set<String> keys() {
        return mJSONObjectRoot.keySet();
    }

    @Override
    public List<Object> values() {
        return Collections.singletonList(getAll().values());
    }
}
