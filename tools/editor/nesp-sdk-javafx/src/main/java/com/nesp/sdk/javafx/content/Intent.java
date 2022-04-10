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
