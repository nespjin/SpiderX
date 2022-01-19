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
