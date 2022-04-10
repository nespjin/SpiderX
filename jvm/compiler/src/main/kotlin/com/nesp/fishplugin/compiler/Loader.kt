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

package com.nesp.fishplugin.compiler

import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.Result
import com.nesp.fishplugin.core.data.Page2
import com.nesp.fishplugin.core.data.Page2.Companion.plus
import com.nesp.fishplugin.core.data.Plugin2
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FileUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path

/**
 * The plugin loader
 */
object Loader {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(6000L, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Load the plugin
     */
    @JvmStatic
    fun load(plugin: Plugin2): LoadResult {
        if (!Environment.shared.isSupport(plugin)) {
            return LoadResult(Result.CODE_FAILED, "Plugin is not support current device type")
        }
        return LoadResult(Result.CODE_SUCCESS, data = plugin)
    }

    /**
     * Load plugin from disk
     */
    @JvmStatic
    fun loadPluginFromDisk(path: String): LoadResult {
        val pluginFile = File(path)
        if (!pluginFile.exists() || !pluginFile.isFile)
            return LoadResult(Result.CODE_FAILED, "Load plugin failed from path:$path")
        val pluginString = String(pluginFile.readBytes(), StandardCharsets.UTF_8)
        val json = loadPluginFromJsonString(pluginString)
        if (json.data != null) {
            return load(json.data!!)
        }
        return json
    }

    /**
     * Load plugin from url
     */
    @JvmStatic
    fun loadPluginFromUrl(url: String): LoadResult {
        val request = Request.Builder().get().url(url).build()
        val response = httpClient.newCall(request).execute()
        val code = response.code
        val responseBody = response.body
            ?: return LoadResult(Result.CODE_FAILED, "Load plugin failed from url:$url")
        if (code != 400) return LoadResult(Result.CODE_FAILED, "Load plugin failed from url:$url")
        val json = loadPluginFromJsonString(responseBody.string())
        if (json.data != null) {
            return load(json.data!!)
        }
        return json
    }

    /**
     * Create instance of plugin and pick the correct field value according to device type.
     */
    private fun loadPluginFromJsonString(jsonString: String): LoadResult {
        if (jsonString.isEmpty()) return LoadResult(Result.CODE_FAILED, "The json is empty")
        try {
            val plugin = Plugin2(JSONObject(jsonString))
            if (plugin.parent != null) {
                val loadParent = loadParent(plugin.parent!!)
                if (loadParent.code == Result.CODE_SUCCESS) {
                    plugin.parent = loadParent.data
                } else {
                    return loadParent
                }
            }

            // Apply parent's fields
            val applyParentFieldsErrorMsg = applyParentFields(plugin)
            if (applyParentFieldsErrorMsg.isNotEmpty()) {
                return LoadResult(Result.CODE_FAILED, applyParentFieldsErrorMsg)
            }

            // Remove parent if apply parent's fields success
            plugin.parent = null

            return LoadResult(Result.CODE_SUCCESS, data = plugin)
        } catch (e: JSONException) {
            return LoadResult(Result.CODE_FAILED, "error occurs when parsing plugin")
        }
    }

    private fun loadParent(parent: Any): LoadResult {
        // Handle parent plugin
        if (parent is String) {
            // load parent plugin from ref path.
            if (parent.startsWith("http://", true)
                || parent.startsWith("https://", true)
            ) {
                val loadPluginFromUrl = loadPluginFromUrl(parent)
                if (loadPluginFromUrl.code != Result.CODE_SUCCESS) {
                    return loadPluginFromUrl
                }
                return loadPluginFromUrl
            } else {
                val loadPluginFromDisk = loadPluginFromDisk(parent)
                if (loadPluginFromDisk.code != Result.CODE_SUCCESS) {
                    return loadPluginFromDisk
                }
                return loadPluginFromDisk
            }
        } else {
            val loadParentPluginFromJsonString = loadPluginFromJsonString(parent.toString())
            if (loadParentPluginFromJsonString.code != Result.CODE_SUCCESS) {
                return loadParentPluginFromJsonString
            }
            return loadParentPluginFromJsonString
        }
    }

    /**
     * Returns error msg
     */
    private fun applyParentFields(plugin: Plugin2): String {
        val deviceTypes = Environment.allDeviceTypes()
        for (deviceType in deviceTypes) {
            val ret = applyParentFieldsForDeviceType(plugin)
            if (ret.isNotEmpty()) return ret
        }

        val ret = applyParentFieldsForDeviceType(plugin)
        if (ret.isNotEmpty()) return ret

        return ""
    }

    private fun applyParentFieldsForDeviceType(plugin: Plugin2): String {
        val pluginParent = plugin.parent
        if (pluginParent != null && pluginParent !is Plugin2) {
            return "The parent is not loaded"
        }

        var parent: Any?

        // name
        if (plugin.name.isEmpty()) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.name.isNotEmpty()) {
                    plugin.name = parent.name
                    break
                }
                parent = parent.parent
            }
        }

        // version
        if (plugin.version.isEmpty()) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.version.isNotEmpty()) {
                    plugin.version = parent.version
                    break
                }
                parent = parent.parent
            }
        }

        // runtime
        if (plugin.runtime.isEmpty()) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.runtime.isNotEmpty()) {
                    plugin.runtime = parent.runtime
                    break
                }
                parent = parent.parent
            }
        }

        // time
        if (plugin.time.isEmpty()) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.time.isNotEmpty()) {
                    plugin.time = parent.time
                    break
                }
                parent = parent.parent
            }
        }

        // tags
        if (plugin.tags.isNullOrEmpty()) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.tags?.isNotEmpty() == true) {
                    plugin.tags = parent.tags
                    break
                }
                parent = parent.parent
            }
        }

        // deviceFlags
        if (plugin.deviceFlags == -1) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.deviceFlags != -1) {
                    plugin.deviceFlags = parent.deviceFlags
                    break
                }
                parent = parent.parent
            }
        }

        // type
        if (plugin.type == -1) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.type != -1) {
                    plugin.type = parent.type
                    break
                }
                parent = parent.parent
            }
        }

        // introduction
        if (plugin.introduction.isEmpty()) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.introduction.isNotEmpty()) {
                    plugin.introduction = parent.introduction
                    break
                }
                parent = parent.parent
            }
        }

        // ref
        parent = pluginParent
        val refsMap = hashMapOf<String/* name */, Any>()
        if (!plugin.ref.isNullOrEmpty()) {
            for ((name, value) in plugin.ref!!) {
                if (refsMap.containsKey(name)) return "Duplicate name($name)"
                refsMap[name] = value
            }
        }
        while (parent != null && parent is Plugin2) {
            if (!plugin.ref.isNullOrEmpty()) {
                for ((nameOfParent, valueOfValue) in parent.ref!!) {
                    if (!refsMap.containsKey(nameOfParent)) {
                        refsMap[nameOfParent] = valueOfValue
                    }
                }
            } else {
                plugin.ref = parent.ref
                if (!plugin.ref.isNullOrEmpty()) {
                    for ((name, value) in plugin.ref!!) {
                        if (refsMap.containsKey(name)) return "Duplicate name($name)"
                        refsMap[name] = value
                    }
                }
            }
            parent = parent.parent
        }
        plugin.ref = refsMap

        // pages
        parent = pluginParent
        val pagesMap = hashMapOf<String/* id */, Page2>()
        if (plugin.pages.isNotEmpty()) {
            for (page in plugin.pages) {
                if (pagesMap.containsKey(page.id)) return "Duplicate page id(${page.id})"
                pagesMap[page.id] = page
            }
        }
        while (parent != null && parent is Plugin2) {
            if (plugin.pages.isNotEmpty()) {
                for (pageOfParent in parent.pages) {
                    if (!pagesMap.containsKey(pageOfParent.id)) {
                        pagesMap[pageOfParent.id] = pageOfParent
                    } else {
                        pagesMap[pageOfParent.id] += pageOfParent
                    }
                }
            } else {
                plugin.pages = parent.pages
                if (plugin.pages.isNotEmpty()) {
                    for (page in plugin.pages) {
                        if (pagesMap.containsKey(page.id)) return "Duplicate page id(${page.id})"
                        pagesMap[page.id] = page
                    }
                }
            }
            parent = parent.parent

            plugin.pages = arrayListOf<Page2>().apply { addAll(pagesMap.values) }
            pagesMap.clear()
        }

        // extensions
        if (plugin.extensions == null
            || (plugin.extensions is Map<*, *> && (plugin.extensions as Map<*, *>).isEmpty())
        ) {
            parent = pluginParent
            while (parent != null && parent is Plugin2) {
                if (parent.extensions != null) {
                    if (parent.extensions is Map<*, *>) {
                        if ((parent.extensions as Map<*, *>).isNotEmpty()) {
                            plugin.extensions = parent.extensions
                            break
                        }
                    } else {
                        plugin.extensions = parent.extensions
                        break
                    }
                }
                parent = parent.parent
            }
        }
        return ""
    }

    private fun loadPageFromJsonObject(jsonPageRoot: JSONObject?): LoadPageResult {
        if (jsonPageRoot == null) {
            return LoadPageResult(Result.CODE_FAILED, "Json object is null.")
        }
        return LoadPageResult(Result.CODE_SUCCESS, data = Page2(jsonPageRoot))
    }

    /**
     * Load page from url
     */
    fun loadPageFromUrl(url: String): LoadPageResult {
        val request = try {
            Request.Builder().get().url(url).build()
        } catch (e: Exception) {
            return LoadPageResult(Result.CODE_FAILED, "Load page failed from url:$url")
        }
        val response = httpClient.newCall(request).execute()
        val code = response.code
        val responseBody = response.body
            ?: return LoadPageResult(Result.CODE_FAILED, "Load page failed from url:$url")
        if (code != 400) return LoadPageResult(Result.CODE_FAILED, "Load page failed from url:$url")
        return loadPageFromJsonObject(JSONObject(responseBody.string()))
    }

    /**
     * Load Js from disk
     */
    fun loadJsFromDisk(path: String): LoadJsResult {
        val jsFile = Path(path).toFile()
        if (!jsFile.exists() || !jsFile.isFile) {
            return LoadJsResult(Result.CODE_FAILED, "Load failed from path: $path")
        }
        val jsString = FileUtils.readFileToString(jsFile, "UTF-8")
        return LoadJsResult(Result.CODE_SUCCESS, data = jsString)
    }

    /**
     * Load js from url
     */
    fun loadJsFromUrl(url: String): LoadJsResult {
        val request = Request.Builder().get().url(url).build()
        val response = httpClient.newCall(request).execute()
        val code = response.code
        val responseBody = response.body
            ?: return LoadJsResult(Result.CODE_FAILED, "Load failed from url: $url")
        if (code != 400) return LoadJsResult(
            Result.CODE_FAILED,
            "Load plugin failed from url: $url"
        )
        return LoadJsResult(Result.CODE_SUCCESS, data = responseBody.string())
    }

    class LoadJsResult(
        code: Int = CODE_FAILED,
        message: String = "",
        data: String? = null,
    ) : Result<String>(code, "load js: $message", data)

    class LoadPageResult(
        code: Int = CODE_FAILED,
        message: String = "",
        data: Page2? = null,
    ) : Result<Page2>(code, "load page: $message", data)

    class LoadResult(
        code: Int = CODE_FAILED,
        message: String = "",
        data: Plugin2? = null,
    ) : Result<Plugin2>(code, "load plugin: $message", data)
}