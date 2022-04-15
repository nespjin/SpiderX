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

import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.FieldName
import com.nesp.fishplugin.core.PluginUtil
import com.nesp.fishplugin.core.utils.JSONUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 * Fish Plugin
 */
class Plugin2 constructor(val store: JSONObject = JSONObject()) {

    private val _pagesCache by lazy { arrayListOf<Page2>() }

    init {
        for (page in pages) {
            page.owner = this
        }
    }

    var parent: Any?
        set(value) {
            store.put(FIELD_NAME_PARENT, value)
        }
        get() {
            return store.opt(FIELD_NAME_PARENT)
        }

    var name: String
        set(value) {
            store.put(FIELD_NAME_NAME, value)
        }
        get() {
            return store.optString(FIELD_NAME_NAME)
        }

    var id: String
        set(value) {
            store.put(FIELD_NAME_ID, value)
        }
        get() {
            return store.optString(FIELD_NAME_ID)
        }

    var author: String
        set(value) {
            store.put(FIELD_NAME_AUTHOR, value)
        }
        get() {
            return store.optString(FIELD_NAME_AUTHOR)
        }

    var version: String
        set(value) {
            store.put(FIELD_NAME_VERSION, value)
        }
        get() {
            return store.optString(FIELD_NAME_VERSION)
        }

    var runtime: String
        set(value) {
            store.put(FIELD_NAME_RUNTIME, value)
        }
        get() {
            return store.optString(FIELD_NAME_RUNTIME)
        }

    var time: String
        set(value) {
            store.put(FIELD_NAME_TIME, value)
        }
        get() {
            return store.optString(FIELD_NAME_TIME)
        }

    var tags: List<String>
        set(value) {
            try {
                store.put(FIELD_NAME_TAGS, value)
            } catch (e: NoSuchMethodError) {
                store.put(FIELD_NAME_TAGS, JSONArray(value))
            }
        }
        get() {
            val tmp = store.optJSONArray(FIELD_NAME_TAGS) ?: return emptyList()
            val originList = try {
                tmp.toList()
            } catch (e: NoSuchMethodError) {
                JSONUtil.toList(tmp)
            }

            if (originList.isEmpty()) return emptyList()
            val ret = mutableListOf<String>()
            for (item in originList) {
                if (item !is String) {
                    throw IllegalStateException("The type ${item::class.java.simpleName} is not supported")
                }
                ret.add(item)
            }
            return ret
        }

    var deviceFlags: Int
        set(value) {
            store.put(FIELD_NAME_DEVICE_FLAGS, value)
        }
        get() {
            return store.optInt(FIELD_NAME_DEVICE_FLAGS, -1)
        }

    var type: Int
        set(value) {
            store.put(FIELD_NAME_TYPE, value)
        }
        get() {
            return store.optInt(FIELD_NAME_TYPE, -1)
        }

    var introduction: String
        set(value) {
            store.put(FIELD_NAME_INTRODUCTION, value)
        }
        get() {
            return store.optString(FIELD_NAME_INTRODUCTION)
        }

    var ref: Map<String, Any>?
        set(value) {
            store.put(FIELD_NAME_REF, value)
        }
        get() {
            return store.optJSONObject(FIELD_NAME_REF)?.toMap()
        }

    fun applyPages() {
        pages = _pagesCache
    }

    var pages: List<Page2>
        set(value) {
            val ret = JSONArray()
            for (page2 in value) {
                ret.put(page2.store)
            }
            store.put(FIELD_NAME_PAGES, ret)
        }
        get() {
            val tmp = store.optJSONArray(FIELD_NAME_PAGES) ?: return emptyList()
            val originList = try {
                tmp.toList()
            } catch (e: NoSuchMethodError) {
                JSONUtil.toList(tmp)
            }

            if (originList.isNullOrEmpty()) return emptyList()
            val ret = mutableListOf<Page2>()
            for (item in originList) {
                if (item !is Page2) {
                    if (item is Map<*, *>) {
                        ret.add(Page2(JSONObject(item)).apply {
                            owner = this@Plugin2
                        })
                    } else {
                        throw IllegalStateException("The type ${item::class.java.simpleName} is not supported")
                    }
                } else {
                    item.owner = this@Plugin2
                    ret.add(item)
                }
            }

            if (_pagesCache.isEmpty()) {
                _pagesCache.addAll(ret)
            } else {
                for (i in 0 until _pagesCache.size) {
                    val page2 = _pagesCache[i]
                    val originPage2 = ret.find { it.id == page2.id }
                    if (originPage2 != null) {
                        page2.store = originPage2.store
                    }
                }
            }

            return _pagesCache
        }

    var extensions: Any?
        set(value) {
            store.put(FIELD_NAME_EXTENSIONS, value)
        }
        get() {
            return store.opt(FIELD_NAME_EXTENSIONS)
        }

    /**
     * Whether to support mobile phone
     */
    fun isSupportMobilePhone(): Boolean {
        return (deviceFlags and Plugin.DEVICE_FLAG_PHONE) == Plugin.DEVICE_FLAG_PHONE
    }

    /**
     * Whether to support table
     */
    fun isSupportTable(): Boolean {
        return (deviceFlags and Plugin.DEVICE_FLAG_TABLE) == Plugin.DEVICE_FLAG_TABLE
    }

    /**
     * Whether to support desktop
     */
    fun isSupportDesktop(): Boolean {
        return (deviceFlags and Plugin.DEVICE_FLAG_DESKTOP) == Plugin.DEVICE_FLAG_DESKTOP
    }

    /**
     * Find variable which named [variableName] from [ref]
     */
    fun findRefVariable(variableName: String, deviceType: Int? = null): Any? {
        if (ref.isNullOrEmpty()) return null
        if (variableName.trim().isEmpty()) return null
        val key = PluginUtil.getFieldNameWithDeviceType(variableName, deviceType)
        if (!ref!!.containsKey(key)) return null
        return ref!![key]
    }

    fun getFieldValue(fieldName: String): Any? {
        return when (fieldName) {
            FIELD_NAME_NAME -> this.name
            FIELD_NAME_ID -> this.id
            FIELD_NAME_AUTHOR -> this.author
            FIELD_NAME_VERSION -> this.version
            FIELD_NAME_RUNTIME -> this.runtime
            FIELD_NAME_TIME -> this.time
            FIELD_NAME_TAGS -> this.tags
            FIELD_NAME_DEVICE_FLAGS -> this.deviceFlags
            FIELD_NAME_TYPE -> this.type
            FIELD_NAME_INTRODUCTION -> this.introduction
            FIELD_NAME_REF -> this.ref
            FIELD_NAME_PAGES -> this.pages
            FIELD_NAME_EXTENSIONS -> this.extensions
            else -> ""
        }
    }

    fun setFieldValue(fieldName: String, fieldValue: Any?) {
        when (fieldName) {
            FIELD_NAME_NAME -> {
                if (fieldValue is String) this.name = fieldValue
            }
            FIELD_NAME_ID -> {
                if (fieldValue is String) this.id = fieldValue
            }
            FIELD_NAME_AUTHOR -> {
                if (fieldValue is String) this.author = fieldValue
            }
            FIELD_NAME_VERSION -> {
                if (fieldValue is String) this.version = fieldValue
            }
            FIELD_NAME_RUNTIME -> {
                if (fieldValue is String) this.runtime = fieldValue
            }
            FIELD_NAME_TIME -> {
                if (fieldValue is String) this.time = fieldValue
            }
            FIELD_NAME_TAGS -> {
                if (fieldValue is Collection<*>) {
                    val arrayList = arrayListOf<String>()
                    @Suppress("UNCHECKED_CAST")
                    arrayList.addAll(fieldValue as Collection<String>)
                    this.tags = arrayList
                }
            }
            FIELD_NAME_DEVICE_FLAGS -> {
                if (fieldValue is Int) this.deviceFlags = fieldValue
            }
            FIELD_NAME_TYPE -> {
                if (fieldValue is Int) this.type = fieldValue
            }
            FIELD_NAME_INTRODUCTION -> {
                if (fieldValue is String) this.introduction = fieldValue
            }
            FIELD_NAME_REF -> {
                if (fieldValue == null || fieldValue is Map<*, *>) {
                    try {
                        this.ref = fieldValue as Map<String, Any>?
                    } catch (ignored: Exception) {
                    }
                }
            }
            FIELD_NAME_PAGES -> {
                if (fieldValue is Collection<*>) {
                    try {
                        val arrayList = arrayListOf<Page2>()
                        arrayList.addAll(fieldValue as Collection<Page2>)
                        this.pages = arrayList
                    } catch (ignored: Exception) {
                    }
                }
            }
            FIELD_NAME_EXTENSIONS -> this.extensions = fieldValue
            else -> {}
        }
    }

    fun findPageById(id: String): Page2? {
        return pages.firstOrNull { it.id == id }
    }

    fun findPage(predicate: (Page2) -> Boolean): Page2? {
        return pages.firstOrNull(predicate)
    }


    /**
     * Returns error message
     */
    fun checkFields(): String {
        val fieldNames = getFieldNames()
        val entries = store.toMap().entries
        for (entry in entries) {
            if (entry.key !in fieldNames) {
                return "The field ${entry.key} is not supported"
            }
        }
        return ""
    }

    fun isSupport(deviceType: Int?): Boolean {
        return when (deviceType) {
            Environment.DEVICE_TYPE_MOBILE_PHONE -> {
                isSupportMobilePhone()
            }
            Environment.DEVICE_TYPE_TABLE -> {
                isSupportTable()
            }
            Environment.DEVICE_TYPE_DESKTOP -> {
                isSupportDesktop()
            }
            else -> false
        }
    }

    companion object {

        const val DEVICE_FLAG_PHONE = 1 shl 0
        const val DEVICE_FLAG_TABLE = 1 shl 1
        const val DEVICE_FLAG_DESKTOP = 1 shl 2

        ///////////////////////////////////////////////////////////////////////////
        // FIELD NAME
        ///////////////////////////////////////////////////////////////////////////
        /**
         * The parent plugin.
         * The current plugin is called a child plugin relative to the parent plugin,
         * If a field exists in both the parent plugin and the child plugin, use the field in the child plugin
         * The types:
         * 1. Plugin: Map<String,Any?> or entity
         * 2. Local path, C:/xx/xx/xx/SamplePlugin.json
         * 3. Http path, https://xxx/xxx/SamplePlugin.json
         */
        @FieldName
        const val FIELD_NAME_PARENT = "parent"

        /**
         * plugin name
         */
        @FieldName
        const val FIELD_NAME_NAME = "name"

        /**
         * The plugin id
         */
        @FieldName
        const val FIELD_NAME_ID = "id"

        /**
         * The author
         */
        @FieldName
        const val FIELD_NAME_AUTHOR = "author"

        /**
         * Version: version name + version code
         * for example:
         * 1.0.0+1
         */
        @FieldName
        const val FIELD_NAME_VERSION = "version"

        /**
         * Supported runtime, double closed interval
         * Format: minimum version supported - highest version supported
         */
        @FieldName
        const val FIELD_NAME_RUNTIME = "runtime"

        /**
         * creation time, update time
         * For example: 2021-11-30 22:00:11,2022-01-01 08:00:11
         */
        @FieldName
        const val FIELD_NAME_TIME = "time"

        /**
         * The tags of plugin
         */
        @FieldName
        const val FIELD_NAME_TAGS = "tags"

        /**
         *  Supported device types (mobile phone, tablet, computer) 8bit
         *
         *  bit7 bit6 bit5 bit4 bit3 bit2 bit1 bit0
         *  0    0    0    0    0    0    0    0
         *
         *  bit0: Whether to support mobile phone, 0 not support 1 support
         *  bit1: Whether to support tablet, 0 not support 1 support
         *  bit2: Whether to support desktop, 0 not support 1 support
         *
         *  For example: the value 00000001 supports mobile phones only,
         *  the value 00000011 supports mobile phones and tablets
         */
        @FieldName
        const val FIELD_NAME_DEVICE_FLAGS = "deviceFlags"

        /**
         * Type, see the fields of starts with 'TYPE_'
         */
        @FieldName
        const val FIELD_NAME_TYPE = "type"

        /**
         * Plugin introduction
         */
        @FieldName
        const val FIELD_NAME_INTRODUCTION = "introduction"

        /**
         * Custom references can be customized fields, objects, js code
         */
        @FieldName
        const val FIELD_NAME_REF = "ref"

        /**
         * page collection
         */
        @FieldName
        const val FIELD_NAME_PAGES = "pages"

        @JvmStatic
        fun getFieldNames(): List<String> {
            return PluginUtil.getFieldNames(Plugin2::class.java)
        }

        /**
         * Extend Object
         */
        @FieldName
        const val FIELD_NAME_EXTENSIONS = "extensions" /* Map<String,Any?> or entity */

        ///////////////////////////////////////////////////////////////////////////
        // Http
        ///////////////////////////////////////////////////////////////////////////
        const val HTTP_REQ_TYPE_PREFIX_GET = "get:"
        const val HTTP_REQ_TYPE_PREFIX_POST = "post:"

        @JvmStatic
        fun isGetReq(url: String): Boolean {
            return url.startsWith(HTTP_REQ_TYPE_PREFIX_GET)
        }

        @JvmStatic
        fun isPostReq(url: String): Boolean {
            return url.startsWith(HTTP_REQ_TYPE_PREFIX_POST)
        }

        @JvmStatic
        fun removeReqPrefix(url: String): String {
            if (isGetReq(url)) {
                return url.substring(HTTP_REQ_TYPE_PREFIX_GET.length)
            } else if (isPostReq(url)) {
                return url.substring(HTTP_REQ_TYPE_PREFIX_POST.length)
            }
            return url
        }

        ///////////////////////////////////////////////////////////////////////////
        // Plugin Type
        ///////////////////////////////////////////////////////////////////////////
        const val TYPE_MOVIE = 0
    }
}