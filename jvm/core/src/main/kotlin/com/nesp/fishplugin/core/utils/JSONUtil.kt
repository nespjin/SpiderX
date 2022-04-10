/*
 * Copyright (c) 2022-2022.  NESP Technology.
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

package com.nesp.fishplugin.core.utils

import org.json.JSONArray
import org.json.JSONObject

/**
 * Team: NESP Technology
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * @version 1.0
 * Time: Created 2022/4/8 8:56 PM
 * Description:
 **/
object JSONUtil {

    fun toList(jsonArray: JSONArray): List<Any?> {
        val length = jsonArray.length()
        val results: ArrayList<Any?> = ArrayList(length)
        for (i in 0 until length) {
            val element = jsonArray.get(i)
            if (element == null || JSONObject.NULL == element) {
                results.add(null)
            } else if (element is JSONArray) {
                results.add((element as JSONArray).toList())
            } else if (element is JSONObject) {
                results.add(toMap(element as JSONObject))
            } else {
                results.add(element)
            }
        }
        return results
    }

    fun toMap(jsonObject: JSONObject): Map<String, Any?> {
        val results: MutableMap<String, Any?> = HashMap()
        for (key in jsonObject.keys()) {
            val value1 = jsonObject.get(key)
            var value: Any?
            value = if (value1 == null || JSONObject.NULL == value1) {
                null
            } else if (value1 is JSONObject) {
                (value1 as JSONObject).toMap()
            } else if (value1 is JSONArray) {
                (value1 as JSONArray).toList()
            } else {
                value1
            }
            results[key] = value
        }
        return results
    }
}