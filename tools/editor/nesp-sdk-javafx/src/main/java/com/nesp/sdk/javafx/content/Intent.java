package com.nesp.sdk.javafx.content;

import java.util.HashMap;
import java.util.Map;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/1 下午3:47
 * Description:
 **/
public final class Intent {

    private final String mAction;
    private Map<String, Object> mData;

    public Intent(final String action) {
        this.mAction = action;
    }

    public String getAction() {
        return mAction;
    }

    public void put(final String key, final Object value) {
        if (mData == null) {
            mData = new HashMap<>();
        }
        mData.putIfAbsent(key, value);
    }

    public Object get(final String key) {
        if (mData == null) return null;
        return mData.get(key);
    }
}
