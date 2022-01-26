package com.nesp.fishplugin.core.data

import com.nesp.fishplugin.core.Environment

/**
 * Fish Plugin
 */
data class Plugin @JvmOverloads constructor(
    /**
     * The parent plugin.
     * The current plugin is called a child plugin relative to the parent plugin,
     * If a field exists in both the parent plugin and the child plugin, use the field in the child plugin
     * The types:
     * 1. Plugin: Map<String,Any?> or entity
     * 2. Local path, C:/xx/xx/xx/SamplePlugin.json
     * 3. Http path, https://xxx/xxx/SamplePlugin.json
     */
    var parent: Any? = null,

    /**
     * plugin name
     */
    var name: String = "",

    /**
     * The plugin id
     */
    var id: String = "",

    /**
     * The author
     */
    var author: String = "",

    /**
     * Version: version name + version code
     * for example:
     * 1.0.0+1
     */
    var version: String = "",

    /**
     * Supported runtime, double closed interval
     * Format: minimum version supported - highest version supported
     */
    var runtime: String = "",

    /**
     * creation time, update time
     * For example: 2021-11-30 22:00:11,2022-01-01 08:00:11
     */
    var time: String = "",

    /**
     * The tags of plugin
     */
    var tags: List<String> = emptyList(),

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
    var deviceFlags: Int = -1,

    /**
     * Type, see the fields of starts with 'TYPE_'
     */
    var type: Int = -1,

    /**
     * Plugin introduction
     */
    var introduction: String? = null,

    /**
     * Custom references can be customized fields, objects, js code
     */
    var ref: Map<String, Any>? = null,

    /**
     * page collection
     */
    var pages: List<Page> = emptyList(),

    /**
     * Extend Object
     */
    var extensions: Any? /* Map<String,Any?> or entity */ = null,
) {

    /**
     * Whether to support mobile phone
     */
    fun isSupportMobilePhone(): Boolean {
        return (deviceFlags and DEVICE_FLAG_PHONE) == DEVICE_FLAG_PHONE
    }

    /**
     * Whether to support table
     */
    fun isSupportTable(): Boolean {
        return (deviceFlags and DEVICE_FLAG_TABLE) == DEVICE_FLAG_TABLE
    }

    /**
     * Whether to support desktop
     */
    fun isSupportDesktop(): Boolean {
        return (deviceFlags and DEVICE_FLAG_DESKTOP) == DEVICE_FLAG_DESKTOP
    }

    /**
     * Find variable which named [variableName] from [ref]
     */
    fun findRefVariable(variableName: String): Any? {
        if (ref.isNullOrEmpty()) return null
        if (variableName.trim().isEmpty()) return null
        if (!ref!!.containsKey(variableName)) return null
        val deviceType = Environment.shared.getDeviceType()
        if (ref!!.containsKey("$variableName-$deviceType")) {
            return ref!!["$variableName-$deviceType"]
        }
        return ref!![variableName]
    }

    fun getFieldValue(fieldName: String): Any? {
        return when (fieldName) {
            FILED_NAME_NAME -> this.name
            FILED_NAME_ID -> this.id
            FILED_NAME_AUTHOR -> this.author
            FILED_NAME_VERSION -> this.version
            FILED_NAME_RUNTIME -> this.runtime
            FILED_NAME_TIME -> this.time
            FILED_NAME_TAGS -> this.tags
            FILED_NAME_DEVICE_FLAGS -> this.deviceFlags
            FILED_NAME_TYPE -> this.type
            FILED_NAME_INTRODUCTION -> this.introduction
            FILED_NAME_REF -> this.ref
            FILED_NAME_PAGES -> this.pages
            FILED_NAME_EXTENSIONS -> this.extensions
            else -> ""
        }
    }

    fun setFieldValue(fieldName: String, fieldValue: Any?) {
        when (fieldName) {
            FILED_NAME_NAME -> {
                if (fieldValue is String) this.name = fieldValue
            }
            FILED_NAME_ID -> {
                if (fieldValue is String) this.id = fieldValue
            }
            FILED_NAME_AUTHOR -> {
                if (fieldValue is String) this.author = fieldValue
            }
            FILED_NAME_VERSION -> {
                if (fieldValue is String) this.version = fieldValue
            }
            FILED_NAME_RUNTIME -> {
                if (fieldValue is String) this.runtime = fieldValue
            }
            FILED_NAME_TIME -> {
                if (fieldValue is String) this.time = fieldValue
            }
            FILED_NAME_TAGS -> {
                if (fieldValue is Collection<*>) {
                    val arrayList = arrayListOf<String>()
                    @Suppress("UNCHECKED_CAST")
                    arrayList.addAll(fieldValue as Collection<String>)
                    this.tags = arrayList
                }
            }
            FILED_NAME_DEVICE_FLAGS -> {
                if (fieldValue is Int) this.deviceFlags = fieldValue
            }
            FILED_NAME_TYPE -> {
                if (fieldValue is Int) this.type = fieldValue
            }
            FILED_NAME_INTRODUCTION -> {
                if (fieldValue is String) this.introduction = fieldValue
            }
            FILED_NAME_REF -> {
                if (fieldValue == null || fieldValue is Map<*, *>) {
                    try {
                        this.ref = fieldValue as Map<String, Any>?
                    } catch (ignored: Exception) {
                    }
                }
            }
            FILED_NAME_PAGES -> {
                if (fieldValue is Collection<*>) {
                    try {
                        val arrayList = arrayListOf<Page>()
                        arrayList.addAll(fieldValue as Collection<Page>)
                        this.pages = arrayList
                    } catch (ignored: Exception) {
                    }
                }
            }
            FILED_NAME_EXTENSIONS -> this.extensions = fieldValue
            else -> {}
        }
    }

    fun findPageById(id: String): Page? {
        return pages.firstOrNull { it.id == id }
    }

    fun findPage(predicate: (Page) -> Boolean): Page? {
        return pages.firstOrNull(predicate)
    }

    companion object {

        const val DEVICE_FLAG_PHONE = 1 shl 0
        const val DEVICE_FLAG_TABLE = 1 shl 1
        const val DEVICE_FLAG_DESKTOP = 1 shl 2

        ///////////////////////////////////////////////////////////////////////////
        // FILED NAME
        ///////////////////////////////////////////////////////////////////////////
        const val FILED_NAME_PARENT = "parent"
        const val FILED_NAME_NAME = "name"
        const val FILED_NAME_ID = "id"
        const val FILED_NAME_AUTHOR = "author"
        const val FILED_NAME_VERSION = "version"
        const val FILED_NAME_RUNTIME = "runtime"
        const val FILED_NAME_TIME = "time"
        const val FILED_NAME_TAGS = "tags"
        const val FILED_NAME_DEVICE_FLAGS = "deviceFlags"
        const val FILED_NAME_TYPE = "type"
        const val FILED_NAME_INTRODUCTION = "introduction"
        const val FILED_NAME_REF = "ref"
        const val FILED_NAME_PAGES = "pages"
        const val FILED_NAME_EXTENSIONS = "extensions"

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
