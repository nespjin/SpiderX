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

package com.nesp.fishplugin.core.data

data class Page(
    var id: String = "",
    var refUrl: String? = null,
    var url: String = "",
    var js: String = "",
    var dsl: Any?/*String? or Map<String, Any>? or DSL entity */ = null,
) {

    // TODO: 2022/1/19 Set owner value
    var owner: Plugin? = null

    fun isDslAvailable(): Boolean {
        if (dsl == null) return false
        if (dsl is String && (dsl as String).isNotEmpty()) return false
        if (dsl is Map<*, *> && (dsl as Map<*, *>).isNotEmpty()) return true
        return false
    }

    fun getFieldValue(fieldName: String): Any? {
        return when (fieldName) {
            FIELD_NAME_ID -> this.id
            FIELD_NAME_REF_URL -> this.refUrl
            FIELD_NAME_URL -> this.url
            FIELD_NAME_JS -> this.js
            FIELD_NAME_DSL -> this.dsl
            else -> null
        }
    }

    fun setFieldValue(fieldName: String, fieldValue: Any?) {
        when (fieldName) {
            FIELD_NAME_ID -> {
                if (fieldValue is String) this.id = fieldValue
            }
            FIELD_NAME_REF_URL -> {
                if (fieldValue is String) this.refUrl = fieldValue
            }
            FIELD_NAME_URL -> {
                if (fieldValue is String) this.url = fieldValue
            }
            FIELD_NAME_JS -> {
                if (fieldValue is String) this.js = fieldValue
            }
            FIELD_NAME_DSL -> {
                if (fieldValue is Map<*, *>?) this.dsl = fieldValue as Map<String, Any>?
            }
        }
    }

    companion object {
        const val FIELD_NAME_ID = "id"
        const val FIELD_NAME_REF_URL = "refUrl"
        const val FIELD_NAME_URL = "url"
        const val FIELD_NAME_JS = "js"
        const val FIELD_NAME_DSL = "dsl"

        const val JS_PATH_PREFIX = "path:"
        const val JS_URL_PREFIX = "url:"
    }
}
