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

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/


class PluginManager {

    fun init(
        databasePath: String,
        screenType: ScreenType,
        isCacheEngine: Boolean,
        webviewClass: Class<*>
    ) {
        nativeInit(databasePath, screenType, isCacheEngine, webviewClass)
    }

    fun setScreenType(screenType: ScreenType) {
        nativeSetScreenType(screenType)
    }

    fun installPluginJson(json: String) {
        nativeInstallPlugin(PLUGIN_SOURCE_TYPE_JSON, json.toByteArray())
    }

    fun installPluginJsonFile(jsonFilePath: String) {
        nativeInstallPlugin(PLUGIN_SOURCE_TYPE_JSON_FILE, jsonFilePath.toByteArray())
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

    fun uninstallPlugin(id: String): List<Plugin>? {
        return nativeUninstallPlugin(id)
    }

    fun requestDataset(pluginId: String, datasetId: String): String? {
        return nativeRequestDataset(pluginId, datasetId)
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

    private external fun nativeUninstallPlugin(id: String): List<Plugin>?

    private external fun nativeRequestDataset(pluginId: String, datasetId: String): String?

    companion object {
        private const val TAG = "PluginManager"

        private const val PLUGIN_SOURCE_TYPE_JSON = 0
        private const val PLUGIN_SOURCE_TYPE_JSON_FILE = 1

        private const val DYNAMIC_LIB_NAME = "spiderx_runtime"

        init {
            System.loadLibrary(DYNAMIC_LIB_NAME)
        }
    }
}