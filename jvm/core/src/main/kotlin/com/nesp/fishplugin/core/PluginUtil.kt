package com.nesp.fishplugin.core

object PluginUtil {

    fun getFieldNameWithDeviceType(fieldName: String, deviceType: Int?): String {
        if (deviceType == null) return fieldName
        return "${fieldName}_$deviceType"
    }
}