package com.nesp.fishplugin.core.data

import com.nesp.fishplugin.core.PluginUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 * Fish Plugin
 */
class Plugin2 {

    private val data = JSONObject()

    fun setParent(value: Any?, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_PARENT, deviceType), value)
    }

    fun getParent(deviceType: Int? = null): Any? {
        return data.opt(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_PARENT, deviceType))
    }

    fun setName(value: String, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_NAME, deviceType), value)
    }

    fun getName(deviceType: Int? = null): String? {
        return data.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_NAME, deviceType))
    }

    fun setId(value: String, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_ID, deviceType), value)
    }

    fun getId(deviceType: Int? = null): String? {
        return data.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_ID, deviceType))
    }

    fun setAuthor(value: String, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_AUTHOR, deviceType), value)
    }

    fun getAuthor(deviceType: Int? = null): String? {
        return data.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_AUTHOR, deviceType))
    }

    fun setVersion(value: String, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_VERSION, deviceType), value)
    }

    fun getVersion(deviceType: Int? = null): String? {
        return data.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_VERSION, deviceType))
    }

    fun setRuntime(value: String, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_RUNTIME, deviceType), value)
    }

    fun getRuntime(deviceType: Int? = null): String? {
        return data.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_RUNTIME, deviceType))
    }

    fun setTime(value: String, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_TIME, deviceType), value)
    }

    fun getTime(deviceType: Int? = null): String? {
        return data.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_TIME, deviceType))
    }

    fun setTags(value: JSONArray, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_TAGS, deviceType), value)
    }

    fun getTags(deviceType: Int? = null): JSONArray? {
        return data.optJSONArray(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_TAGS, deviceType))
    }

    fun setDeviceFlags(value: Int, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_DEVICE_FLAGS, deviceType), value)
    }

    fun getDeviceFlags(deviceType: Int? = null): Int {
        return data.optInt(
            PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_DEVICE_FLAGS, deviceType), -1)
    }

    fun setType(value: Int, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_TYPE, deviceType), value)
    }

    fun getType(deviceType: Int? = null): Int {
        return data.optInt(
            PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_TYPE, deviceType), -1)
    }

    fun setIntroduction(value: String, deviceType: Int? = null) {
        data.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_INTRODUCTION, deviceType), value)
    }

    fun getIntroduction(deviceType: Int? = null): String? {
        return data.optString(
            PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_INTRODUCTION, deviceType))
    }

    companion object {

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
        const val FIELD_NAME_PARENT = "parent"

        /**
         * plugin name
         */
        const val FIELD_NAME_NAME = "name"

        /**
         * The plugin id
         */
        const val FIELD_NAME_ID = "id"

        /**
         * The author
         */
        const val FIELD_NAME_AUTHOR = "author"

        /**
         * Version: version name + version code
         * for example:
         * 1.0.0+1
         */
        const val FIELD_NAME_VERSION = "version"

        /**
         * Supported runtime, double closed interval
         * Format: minimum version supported - highest version supported
         */
        const val FIELD_NAME_RUNTIME = "runtime"

        /**
         * creation time, update time
         * For example: 2021-11-30 22:00:11,2022-01-01 08:00:11
         */
        const val FIELD_NAME_TIME = "time"

        /**
         * The tags of plugin
         */
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
        const val FIELD_NAME_DEVICE_FLAGS = "deviceFlags"

        /**
         * Type, see the fields of starts with 'TYPE_'
         */
        const val FIELD_NAME_TYPE = "type"

        /**
         * Plugin introduction
         */
        const val FIELD_NAME_INTRODUCTION = "introduction"

        /**
         * Custom references can be customized fields, objects, js code
         */
        const val FIELD_NAME_REF = "ref"

        /**
         * page collection
         */
        const val FIELD_NAME_PAGES = "pages"

        /**
         * Extend Object
         */
        const val FIELD_NAME_EXTENSIONS = "extensions"

    }
}