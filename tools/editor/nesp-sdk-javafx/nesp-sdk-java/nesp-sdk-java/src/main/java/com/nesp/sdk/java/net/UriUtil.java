/*
 * Copyright 2021 NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.net;

import com.nesp.sdk.java.text.TextUtil;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/23 上午9:37
 * Description:
 **/
public final class UriUtil {

    private UriUtil() {/*no-op*/}

    private static final String TAG = "UriUtil";

    public static Map<String, String> params(final URI uri) {
        if (uri == null) return Collections.emptyMap();
        final String query = uri.getQuery();
        if (TextUtil.isEmpty(query)) return Collections.emptyMap();

        final Map<String, String> params = new HashMap<>();
        final String[] paramPairs = query.split("&");
        for (final String paramPair : paramPairs) {
            final String[] paramPairArray = paramPair.split("=");
            if (paramPairArray.length < 2) {
                continue;
            }
            params.put(paramPairArray[0], paramPairArray[1]);
        }
        return params;
    }

}
