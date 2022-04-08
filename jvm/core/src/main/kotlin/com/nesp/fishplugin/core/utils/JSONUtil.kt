/*
 *
 *   Copyright (c) 2022  NESP Technology Corporation. All rights reserved.
 *
 *   This program is not free software; you can't redistribute it and/or modify it
 *   without the permit of team manager.
 *
 *   Unless required by applicable law or agreed to in writing.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
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