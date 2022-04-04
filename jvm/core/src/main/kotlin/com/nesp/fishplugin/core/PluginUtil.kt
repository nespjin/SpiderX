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