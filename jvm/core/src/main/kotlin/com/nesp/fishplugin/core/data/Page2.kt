package com.nesp.fishplugin.core.data

import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.FieldName
import com.nesp.fishplugin.core.PluginUtil
import org.json.JSONObject

/**
 * Team: NESP Technology
 * @author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/4/1 10:17 PM
 * Description:
 **/
class Page2 constructor(var store: JSONObject = JSONObject()) {

    var id: String
        set(value) {
            store.put(FIELD_NAME_ID, value)
        }
        get() {
            return store.optString(FIELD_NAME_ID)
        }

    @JvmOverloads
    fun setRefUrl(value: String, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_REF_URL, deviceType), value)
    }

    fun getRefUrl(deviceType: Int? = null): String? {
        return store.optString(
            PluginUtil.getFieldNameWithDeviceType(
                FIELD_NAME_REF_URL,
                deviceType
            )
        )
    }

    fun getAllRefUrls(): Array<String?> {
        val deviceTypes = Environment.allDeviceTypes()
        val ret = arrayOfNulls<String>(deviceTypes.maxOf { it } + 2)
        for (deviceType in deviceTypes) {
            ret[deviceType] = getRefUrl(deviceType)
        }
        ret[ret.lastIndex] = getRefUrl()
        return ret
    }

    fun clearAllRefUrl() {
        val deviceTypes = Environment.allDeviceTypes()
        for (deviceType in deviceTypes) {
            setRefUrl("", deviceType)
        }
        setRefUrl("")
    }

    @JvmOverloads
    fun setUrl(value: String, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_URL, deviceType), value)
    }

    @JvmOverloads
    fun getUrl(deviceType: Int? = null): String {
        return store.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_URL, deviceType))
    }

    fun getAllUrls(): Array<String?> {
        val deviceTypes = Environment.allDeviceTypes()
        val ret = arrayOfNulls<String>(deviceTypes.maxOf { it } + 2)
        for (deviceType in deviceTypes) {
            ret[deviceType] = getUrl(deviceType)
        }
        ret[ret.lastIndex] = getUrl()
        return ret
    }

    fun clearAllUrl() {
        val deviceTypes = Environment.allDeviceTypes()
        for (deviceType in deviceTypes) {
            setUrl("", deviceType)
        }
        setUrl("")
    }

    @JvmOverloads
    fun setJs(value: String, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_JS, deviceType), value)
    }

    @JvmOverloads
    fun getJs(deviceType: Int? = null): String? {
        return store.optString(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_JS, deviceType))
    }

    fun getAllJs(): Array<String?> {
        val deviceTypes = Environment.allDeviceTypes()
        val ret = arrayOfNulls<String>(deviceTypes.maxOf { it } + 2)
        for (deviceType in deviceTypes) {
            ret[deviceType] = getJs(deviceType)
        }
        ret[ret.lastIndex] = getJs()
        return ret
    }

    fun clearAllJs() {
        val deviceTypes = Environment.allDeviceTypes()
        for (deviceType in deviceTypes) {
            setJs("", deviceType)
        }
        setJs("")
    }

    @JvmOverloads
    fun setDsl(value: Any?, deviceType: Int? = null) {
        store.put(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_DSL, deviceType), value)
    }

    @JvmOverloads
    fun getDsl(deviceType: Int? = null): Any? {
        return store.opt(PluginUtil.getFieldNameWithDeviceType(FIELD_NAME_DSL, deviceType))
    }

    fun getAllDsl(): Array<Any?> {
        val deviceTypes = Environment.allDeviceTypes()
        val ret = arrayOfNulls<Any>(deviceTypes.maxOf { it } + 2)
        for (deviceType in deviceTypes) {
            ret[deviceType] = getDsl(deviceType)
        }
        ret[ret.lastIndex] = getDsl()
        return ret
    }

    fun clearAllDsl() {
        val deviceTypes = Environment.allDeviceTypes()
        for (deviceType in deviceTypes) {
            setDsl(null, deviceType)
        }
        setDsl(null)
    }

    // TODO: 2022/1/19 Set owner value
    var owner: Plugin2? = null

    fun isDslAvailable(deviceType: Int? = null): Boolean {
        val dsl = getDsl(deviceType) ?: return false
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
                if (fieldValue is Map<*, *>?) this.setDsl(
                    fieldValue as Map<String, Any>?,
                    deviceType
                )
            }
        }
    }

    /**
     * Returns error message
     */
    fun checkFields(): String {
        val fieldNames = Plugin2.getFieldNames()
        val entries = store.toMap().entries
        for (entry in entries) {
            val deviceTypes = Environment.allDeviceTypes().plus(-1)
            for (fieldName in fieldNames) {
                var isAvailable = false
                for (deviceTypeItem in deviceTypes) {
                    val deviceType = if (deviceTypeItem < 0) null else deviceTypeItem
                    if (entry.key == PluginUtil.getFieldNameWithDeviceType(fieldName, deviceType)
                        && (deviceType == null || owner?.isSupport(deviceType) == true)
                    ) {
                        isAvailable = true
                    }
                }
                if (!isAvailable) {
                    return "The field $fieldName is not supported"
                }
            }
        }
        return ""
    }

    companion object {
        @FieldName
        const val FIELD_NAME_ID = "id"

        @FieldName
        const val FIELD_NAME_REF_URL = "refUrl"

        @FieldName
        const val FIELD_NAME_URL = "url"

        @FieldName
        const val FIELD_NAME_JS = "js"

        @FieldName
        const val FIELD_NAME_DSL = "dsl" /*String? or Map<String, Any>? or DSL entity */

        const val JS_PATH_PREFIX = "path:"
        const val JS_URL_PREFIX = "url:"

        @JvmStatic
        fun getFieldNames(): List<String> {
            return PluginUtil.getFieldNames(Page2::class.java)
        }

        @JvmStatic
        operator fun Page2?.plus(another: Page2): Page2 {
            if (this == null) return another
            val deviceTypes = Environment.allDeviceTypes().plus(-1)
            for (deviceTypeItem in deviceTypes) {
                val deviceType = if (deviceTypeItem < 0) null else deviceTypeItem

                val refUrl1 = another.getRefUrl(deviceType) ?: ""
                if (getRefUrl(deviceType).isNullOrEmpty()) {
                    setRefUrl(refUrl1, deviceType)
                }

                val url1 = another.getUrl(deviceType) ?: ""
                if (getUrl(deviceType).isNullOrEmpty()) {
                    setUrl(url1, deviceType)
                }

                val js1 = another.getJs(deviceType) ?: ""
                if (getJs(deviceType).isNullOrEmpty()) {
                    setJs(js1, deviceType)
                }

                val dsl = getDsl(deviceType)
                val dsl1 = another.getDsl(deviceType)
                if (dsl == null || (dsl is Map<*, *> && dsl.isEmpty())) {
                    setDsl(dsl1, deviceType)
                }

            }
            return another
        }
    }
}