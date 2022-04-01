package com.nesp.fishplugin.core.data

import com.nesp.fishplugin.core.PluginUtil
import org.json.JSONObject

/**
 * Team: NESP Technology
 * @author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/4/1 10:17 PM
 * Description:
 **/
class Page2 {

    private val store = JSONObject()

    var id: String
        set(value) {
            store.put(FIELD_NAME_ID, value)
        }
        get() {
            return store.optString(FIELD_NAME_ID)
        }

    fun setRefUrl(value: String, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_REF_URL, deviceType), value)
    }

    fun getRefUrl(deviceType: Int? = null): String? {
        return store.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_REF_URL, deviceType))
    }

    fun setUrl(value: String, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_URL, deviceType), value)
    }

    fun getUrl(deviceType: Int? = null): String? {
        return store.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_URL, deviceType))
    }

    fun setJs(value: String, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_JS, deviceType), value)
    }

    fun getJs(deviceType: Int? = null): String? {
        return store.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_JS, deviceType))
    }

    fun setDsl(value: Any?, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_DSL, deviceType), value)
    }

    fun getDsl(deviceType: Int? = null): Any? {
        return store.opt(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_DSL, deviceType))
    }

    // TODO: 2022/1/19 Set owner value
    var owner: Plugin? = null

    fun isDslAvailable(deviceType: Int? = null): Boolean {
        val dsl = getDsl(deviceType)
        if (dsl == null) return false
        if (dsl is String && (dsl as String).isNotEmpty()) return false
        if (dsl is Map<*, *> && (dsl as Map<*, *>).isNotEmpty()) return true
        return false
    }

    fun getFieldValue(fieldName: String, deviceType: Int? = null): Any? {
        return when (fieldName) {
            FIELD_NAME_ID -> this.id
            FIELD_NAME_REF_URL -> this.getRefUrl(deviceType)
            FIELD_NAME_URL -> this.getUrl(deviceType)
            FIELD_NAME_JS -> this.getJs(deviceType)
            FIELD_NAME_DSL -> this.getDsl(deviceType)
            else -> null
        }
    }

    fun setFieldValue(fieldName: String, fieldValue: Any?, deviceType: Int? = null) {
        when (fieldName) {
            FIELD_NAME_ID -> {
                if (fieldValue is String) this.id = fieldValue
            }
            FIELD_NAME_REF_URL -> {
                if (fieldValue is String) this.setRefUrl(fieldValue, deviceType)
            }
            FIELD_NAME_URL -> {
                if (fieldValue is String) this.setUrl(fieldValue, deviceType)
            }
            FIELD_NAME_JS -> {
                if (fieldValue is String) this.setJs(fieldValue, deviceType)
            }
            FIELD_NAME_DSL -> {
                if (fieldValue is Map<*, *>?) this.setDsl(fieldValue as Map<String, Any>?, deviceType)
            }
        }
    }

    companion object {
        const val FIELD_NAME_ID = "id"
        const val FIELD_NAME_REF_URL = "refUrl"
        const val FIELD_NAME_URL = "url"
        const val FIELD_NAME_JS = "js"
        const val FIELD_NAME_DSL = "dsl" /*String? or Map<String, Any>? or DSL entity */

        const val JS_PATH_PREFIX = "path:"
        const val JS_URL_PREFIX = "url:"
    }
}