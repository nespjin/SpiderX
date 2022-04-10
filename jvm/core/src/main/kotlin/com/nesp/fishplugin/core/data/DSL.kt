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

/**
 * The Dom selector language
 */
abstract class DSL(private var properties: MutableMap<String/*ignore caps*/, String> = hashMapOf()) {

    init {
        initProperties()
    }

    fun setProperty(name: String, value: String) {
        properties[name.lowercase()] = value
    }

    fun getProperty(name: String): String {
        return properties[name.lowercase()] ?: ""
    }

    fun propertySize(): Int {
        return properties.size
    }

    fun setProperties(properties: MutableMap<String, String>) {
        this.properties = properties
        initProperties()
    }

    fun clearProperties() {
        properties.clear()
    }

    private fun initProperties() {
        // ignore caps
        val propertiesTmp = mutableMapOf<String, String>()
        for (property in properties) {
            propertiesTmp[property.key.lowercase()] = property.value
        }
        properties.clear()
        properties.putAll(propertiesTmp)
    }

    abstract fun isAvailable(): Boolean

    fun isPropertyEmpty(property: Any): Boolean {
        if (property is String) return property.trim().isEmpty()
        if (property is List<*>) return property.isEmpty()
        if (property is Array<*>) return property.isEmpty()
        return false
    }

    companion object {
        const val PREFIX_FISH_DSL = "fdsl:"
        const val PREFIX_SELECTOR = "selector:"
        const val PREFIX_XPATH = "xpath:"
        const val PREFIX_REGEX = "regex:"

        const val DSL_TYPE_FISH_DSL = 0
        const val DSL_TYPE_SELECTOR = 1
        const val DSL_TYPE_XPATH = 2
        const val DSL_TYPE_REGEX = 3

        const val PROPERTY_CONTENT_LIST = "List"
        const val PROPERTY_CONTENT_STATUS = "Status"
        const val PROPERTY_CONTENT_NAME = "Name"
        const val PROPERTY_CONTENT_SCORE = "Score"
        const val PROPERTY_CONTENT_COVER_IMAGE = "CoverImage"
        const val PROPERTY_CONTENT_DETAIL_URL = "DetailUrl"
        const val PROPERTY_CONTENT_URL = "Url"
        const val PROPERTY_CONTENT_PLAY_URL = "PlayUrl"
        const val PROPERTY_CONTENT_TYPE = "Type"
        const val PROPERTY_CONTENT_CATEGORY = "Category"
        const val PROPERTY_CONTENT_STARS = "Stars"
        const val PROPERTY_CONTENT_DIRECTOR = "Director"
        const val PROPERTY_CONTENT_RELEASE_TIME = "ReleaseTime"
        const val PROPERTY_CONTENT_TITLE = "Title"

        @JvmStatic
        fun getDslType(dsl: String): Int {
            when {
                dsl.startsWith(PREFIX_FISH_DSL) -> return DSL_TYPE_FISH_DSL
                dsl.startsWith(PREFIX_SELECTOR) -> return DSL_TYPE_SELECTOR
                dsl.startsWith(PREFIX_XPATH) -> return DSL_TYPE_XPATH
                dsl.startsWith(PREFIX_REGEX) -> return DSL_TYPE_REGEX
            }
            return -1
        }

        @JvmStatic
        fun removeDslTypePrefix(dsl: String): String {
            val len = when {
                dsl.startsWith(PREFIX_FISH_DSL) -> PREFIX_FISH_DSL.length
                dsl.startsWith(PREFIX_SELECTOR) -> PREFIX_SELECTOR.length
                dsl.startsWith(PREFIX_XPATH) -> PREFIX_XPATH.length
                dsl.startsWith(PREFIX_REGEX) -> PREFIX_REGEX.length
                else -> 0
            }
            return dsl.substring(len)
        }
    }
}