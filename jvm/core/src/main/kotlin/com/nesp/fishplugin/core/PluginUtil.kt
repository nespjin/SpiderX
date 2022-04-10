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

package com.nesp.fishplugin.core


object PluginUtil {

    @JvmStatic
    fun getFieldNameWithDeviceType(fieldName: String, deviceType: Int?): String {
        if (deviceType == null) return fieldName
        return "${fieldName}_$deviceType"
    }

    @JvmStatic
    fun getFieldNames(clazz: Class<*>): List<String> {
        val ret = arrayListOf<String>()
        val declaredFields = clazz.declaredFields
        for (declaredField in declaredFields) {
            if (declaredField.name.startsWith("FIELD_NAME_")) {
                ret.add(declaredField.get(null) as String)
            } else {
                val annotations = declaredField.annotations
                if (!annotations.isNullOrEmpty()) {
                    for (annotation in annotations) {
                        if (annotation is FieldName) {
                            ret.add(declaredField.get(null) as String)
                            break
                        }
                    }
                }
            }
        }
        return ret
    }
}