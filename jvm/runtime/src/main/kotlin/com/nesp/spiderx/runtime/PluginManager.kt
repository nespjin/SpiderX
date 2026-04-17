/*
 * Copyright (c) 2025.  NESP Technology.
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

package com.nesp.spiderx.runtime

import com.nesp.spiderx.runtime.model.Plugin
import com.nesp.spiderx.runtime.model.ScreenType
import java.io.File

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/


class PluginManager private constructor() {

    private val cache = mutableMapOf<String, Any>()
    private var screenType: ScreenType = ScreenType.COMPACT
    var requestJavaScriptDatasetConfig: RequestJavaScriptDatasetConfig =
        RequestJavaScriptDatasetConfig()

    fun <T : JniWebView> init(
        databasePath: String,
        screenType: ScreenType,
        isCacheEngine: Boolean,
        webviewClass: Class<T>
    ) {
        this.screenType = screenType
        nativeInit(databasePath, screenType, isCacheEngine, webviewClass)
    }

    fun setCache(key: String, value: Any) {
        cache[key] = value
    }

    fun <T> getCache(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return cache[key] as? T
    }

    fun setAndroidContext(context: Any) {
        setCache("AndroidContext", context)
    }

    fun getAndroidContext(): Any? {
        return getCache("AndroidContext")
    }

    fun setScreenType(screenType: ScreenType) {
        this.screenType = screenType
        nativeSetScreenType(screenType)
    }

    fun getScreenType(): ScreenType {
        return screenType
    }

    fun getUserAgent(): String {
        return if (screenType == ScreenType.COMPACT) {
            "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36"
        } else {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36"
        }
    }

    fun installPluginJson(json: String) {
        nativeInstallPlugin(PLUGIN_SOURCE_TYPE_JSON, json.toByteArray())
    }

    fun installPluginJsonFile(jsonFilePath: String) {
        val jsonBytes = File(jsonFilePath).inputStream().readBytes()
        nativeInstallPlugin(PLUGIN_SOURCE_TYPE_JSON_FILE, jsonBytes)
    }

    private fun installPlugin(sourceType: Int, source: ByteArray) {
        nativeInstallPlugin(sourceType, source)
    }

    fun isPluginInstalled(id: String): Boolean {
        return nativeIsPluginInstalled(id)
    }

    fun getInstalledPlugin(id: String): Plugin? {
        return nativeGetInstalledPlugin(id)
    }

    fun getInstalledPlugins(): List<Plugin>? {
        return nativeGetInstalledPlugins()
    }

    fun uninstallPlugin(id: String) {
        nativeUninstallPlugin(id)
    }

    @JvmOverloads
    fun requestWebsite(
        url: String,
        timeout: UShort = 20u,
        preExecuteJs: String? = null,
        postExecuteJs: String? = null,
        listener: RequestJavaScriptDatasetListener? = null
    ): String? {
        return nativeRequestWebsite(url, timeout.toShort(), preExecuteJs, postExecuteJs, listener)
    }

    @JvmOverloads
    fun requestDataset(
        pluginId: String,
        datasetId: String,
        url: String? = null,
        timeout: UShort = 20u,
        type: RequestType = RequestType.AUTO,
        urlPlaceholders: Map<String, String>? = null,
        listener: RequestDatasetListener? = null,
    ): String? {
        if (listener != null) {
            if (type == RequestType.JavaScript && listener !is RequestJavaScriptDatasetListener) {
                throw IllegalArgumentException("The listener must be RequestJavaScriptDatasetListener when type is JavaScript")
            }
        }
        return nativeRequestDataset(
            pluginId,
            datasetId,
            url,
            timeout.toShort(),
            type.value,
            urlPlaceholders,
            listener
        )
    }


    private external fun nativeInit(
        databasePath: String,
        screenType: ScreenType,
        isCacheEngine: Boolean,
        webviewClass: Class<*>
    )

    private external fun nativeSetScreenType(screenType: ScreenType)

    private external fun nativeInstallPlugin(sourceType: Int, source: ByteArray)

    private external fun nativeIsPluginInstalled(id: String): Boolean

    private external fun nativeGetInstalledPlugin(id: String): Plugin?

    private external fun nativeGetInstalledPlugins(): List<Plugin>?

    private external fun nativeUninstallPlugin(id: String)

    private external fun nativeRequestWebsite(
        url: String,
        timeout: Short,
        preExecuteJs: String?,
        postExecuteJs: String?,
        listener: RequestJavaScriptDatasetListener?
    ): String?

    private external fun nativeRequestDataset(
        pluginId: String,
        datasetId: String,
        url: String?,
        timeout: Short,
        type: Int,
        urlPlaceholders: Map<String, String>?,
        listener: RequestDatasetListener?
    ): String?

    enum class RequestType(val value: Int) {
        AUTO(0), JavaScript(1), DSL(2)
    }

    companion object {
        private const val TAG = "PluginManager"

        private const val PLUGIN_SOURCE_TYPE_JSON = 0
        private const val PLUGIN_SOURCE_TYPE_JSON_FILE = 1

        private const val DYNAMIC_LIB_NAME = "spiderx_runtime"

        init {
            System.loadLibrary(DYNAMIC_LIB_NAME)
        }

        @JvmStatic
        val instance: PluginManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED, ::PluginManager)
    }
}