package com.nesp.fishplugin.compiler

import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.Result
import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.core.data.Page2
import com.nesp.fishplugin.core.data.Plugin
import com.nesp.fishplugin.core.data.Plugin2
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FileUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.Exception
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
        val responseBody =
            response.body ?: return LoadResult(
                Result.CODE_FAILED,
                "Load plugin failed from url:$url"
            )
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
            val jsonPluginRoot = JSONObject(jsonString)
            val plugin = Plugin2(JSONObject(jsonString))

            // Parent
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_PARENT}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_PARENT}-$deviceType")
            ) {
                plugin.parent = jsonPluginRoot["${Plugin.FILED_NAME_PARENT}-$deviceType"]
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_PARENT)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_PARENT)
            ) {
                plugin.parent = jsonPluginRoot[Plugin.FILED_NAME_PARENT]
            }

            // load parent
            if (plugin.parent != null) {
                // Handle parent plugin
                val parent = plugin.parent!!
                if (parent is String) {
                    // load parent plugin from ref path.
                    if (parent.startsWith("http://", true)
                        || parent.startsWith("https://", true)
                    ) {
                        val loadPluginFromUrl = loadPluginFromUrl(parent, deviceType)
                        if (loadPluginFromUrl.code != Result.CODE_SUCCESS) {
                            return loadPluginFromUrl
                        }
                        plugin.parent = loadPluginFromUrl.data
                    } else {
                        val loadPluginFromDisk = loadPluginFromDisk(parent, deviceType)
                        if (loadPluginFromDisk.code != Result.CODE_SUCCESS) {
                            return loadPluginFromDisk
                        }
                        plugin.parent = loadPluginFromDisk.data
                    }
                } else {
                    val loadParentPluginFromJsonString = loadPluginFromJsonString(parent.toString(), deviceType)
                    if (loadParentPluginFromJsonString.code != Result.CODE_SUCCESS) {
                        return loadParentPluginFromJsonString
                    }
                    plugin.parent = loadParentPluginFromJsonString.data
                }
            }

            if (plugin.parent != null) {
                // Apply parent's fields
                val applyParentFieldsErrorMsg = applyParentFields(plugin)
                if (applyParentFieldsErrorMsg.isNotEmpty()) {
                    return LoadResult(Result.CODE_FAILED, applyParentFieldsErrorMsg)
                }
                // Remove parent if apply parent's fields success
                plugin.parent = null
            }

            return LoadResult(Result.CODE_SUCCESS, data = plugin)
        } catch (e: JSONException) {
            return LoadResult(Result.CODE_FAILED, "error occurs when parsing plugin")
        }
    }

    /**
     * Returns error msg
     */
    private fun applyParentFields(plugin: Plugin): String {
        var parent: Any?

        // name
        if (plugin.name.isEmpty()) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.name.isNotEmpty()) {
                    plugin.name = parent.name
                    break
                }
                parent = parent.parent
            }
        }

        // version
        if (plugin.version.isEmpty()) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.version.isNotEmpty()) {
                    plugin.version = parent.version
                    break
                }
                parent = parent.parent
            }
        }

        // runtime
        if (plugin.runtime.isEmpty()) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.runtime.isNotEmpty()) {
                    plugin.runtime = parent.runtime
                    break
                }
                parent = parent.parent
            }
        }

        // time
        if (plugin.time.isEmpty()) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.time.isNotEmpty()) {
                    plugin.time = parent.time
                    break
                }
                parent = parent.parent
            }
        }

        // tags
        if (plugin.tags.isEmpty()) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.tags.isNotEmpty()) {
                    plugin.tags = parent.tags
                    break
                }
                parent = parent.parent
            }
        }

        // deviceFlags
        if (plugin.deviceFlags == -1) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.deviceFlags != -1) {
                    plugin.deviceFlags = parent.deviceFlags
                    break
                }
                parent = parent.parent
            }
        }

        // deviceFlags
        if (plugin.type == -1) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.type != -1) {
                    plugin.type = parent.type
                    break
                }
                parent = parent.parent
            }
        }

        // introduction
        if (plugin.introduction.isNullOrEmpty()) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (!parent.introduction.isNullOrEmpty()) {
                    plugin.introduction = parent.introduction
                    break
                }
                parent = parent.parent
            }
        }

        // ref
        parent = plugin.parent
        val refsMap = hashMapOf<String/* name */, Any>()
        if (!plugin.ref.isNullOrEmpty()) {
            for ((name, value) in plugin.ref!!) {
                if (refsMap.containsKey(name)) return "Duplicate name($name)"
                refsMap[name] = value
            }
        }
        while (parent != null && parent is Plugin) {
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
        parent = plugin.parent
        val pagesMap = hashMapOf<String/* id */, Page>()
        if (plugin.pages.isNotEmpty()) {
            for (page in plugin.pages) {
                if (pagesMap.containsKey(page.id)) return "Duplicate page id(${page.id})"
                pagesMap[page.id] = page
            }
        }
        while (parent != null && parent is Plugin) {
            if (plugin.pages.isNotEmpty()) {
                for (pageOfParent in parent.pages) {
                    if (!pagesMap.containsKey(pageOfParent.id)) {
                        pagesMap[pageOfParent.id] = pageOfParent
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
        }
        plugin.pages = arrayListOf<Page>().apply { addAll(pagesMap.values) }
        pagesMap.clear()

        // extensions
        if (plugin.extensions == null
            || (plugin.extensions is Map<*, *> && (plugin.extensions as Map<*, *>).isEmpty())
        ) {
            parent = plugin.parent
            while (parent != null && parent is Plugin) {
                if (parent.extensions != null) {
                    if (plugin.extensions is Map<*, *>) {
                        if ((plugin.extensions as Map<*, *>).isNotEmpty()) {
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

        val deviceType = Environment.shared.getDeviceType()
        val page = Page()

        // Id
        if (jsonPageRoot.has(Page.FIELD_NAME_ID)
            && !jsonPageRoot.isNull(Page.FIELD_NAME_ID)
        ) {
            (jsonPageRoot.get(Page.FIELD_NAME_ID)?.toString()
                ?: "").also { if (it.isNotEmpty()) page.id = it }
        }

        // Url
        if (jsonPageRoot.has("${Page.FIELD_NAME_URL}-$deviceType")
            && !jsonPageRoot.isNull("${Page.FIELD_NAME_URL}-$deviceType")
        ) {
            (jsonPageRoot.get("${Page.FIELD_NAME_URL}-$deviceType")?.toString()
                ?: "").also { if (it.isNotEmpty()) page.url = it }
        } else if (jsonPageRoot.has(Page.FIELD_NAME_URL)
            && !jsonPageRoot.isNull(Page.FIELD_NAME_URL)
        ) {
            (jsonPageRoot.get(Page.FIELD_NAME_URL)?.toString()
                ?: "").also { if (it.isNotEmpty()) page.url = it }
        }

        // Js
        if (jsonPageRoot.has("${Page.FIELD_NAME_JS}-$deviceType")
            && !jsonPageRoot.isNull("${Page.FIELD_NAME_JS}-$deviceType")
        ) {
            (jsonPageRoot.get("${Page.FIELD_NAME_JS}-$deviceType")?.toString()
                ?: "").also { if (it.isNotEmpty()) page.js = it }
        } else if (jsonPageRoot.has(Page.FIELD_NAME_JS)
            && !jsonPageRoot.isNull(Page.FIELD_NAME_JS)
        ) {
            (jsonPageRoot.get(Page.FIELD_NAME_JS)?.toString()
                ?: "").also { if (it.isNotEmpty()) page.js = it }
        }

        // DSL
        if (jsonPageRoot.has("${Page.FIELD_NAME_DSL}-$deviceType")
            && !jsonPageRoot.isNull("${Page.FIELD_NAME_DSL}-$deviceType")
        ) {
            // String or map
            val map =
                jsonPageRoot.optJSONObject("${Page.FIELD_NAME_DSL}-$deviceType")?.toMap()
            if (map != null) {
                if (map.isNotEmpty()) page.dsl = map
            } else {
                jsonPageRoot.optString("${Page.FIELD_NAME_DSL}-$deviceType", "")?.also {
                    page.dsl = it
                }
            }
        } else if (jsonPageRoot.has(Page.FIELD_NAME_DSL)
            && !jsonPageRoot.isNull(Page.FIELD_NAME_DSL)
        ) {
            // String or map
            val map = jsonPageRoot.optJSONObject(Page.FIELD_NAME_DSL)?.toMap()
            if (map != null) {
                if (map.isNotEmpty()) page.dsl = map
            } else {
                jsonPageRoot.optString(Page.FIELD_NAME_DSL, "")?.also {
                    if (it.isNotEmpty()) page.dsl = it
                }
            }
        }

        // RefUrl
        if (jsonPageRoot.has("${Page.FIELD_NAME_REF_URL}-$deviceType")
            && !jsonPageRoot.isNull("${Page.FIELD_NAME_REF_URL}-$deviceType")
        ) {
            (jsonPageRoot.get("${Page.FIELD_NAME_REF_URL}-$deviceType")?.toString()
                ?: "").also { if (it.isNotEmpty()) page.refUrl = it }
        } else if (jsonPageRoot.has(Page.FIELD_NAME_REF_URL)
            && !jsonPageRoot.isNull(Page.FIELD_NAME_REF_URL)
        ) {
            (jsonPageRoot.optString(Page.FIELD_NAME_REF_URL, "")?.toString()
                ?: "").also { if (it.isNotEmpty()) page.refUrl = it }
        }

        return LoadPageResult(Result.CODE_SUCCESS, data = page)
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
    ) : Result<Page>(code, "load page: $message", data)

    class LoadResult(
        code: Int = CODE_FAILED,
        message: String = "",
        data: Plugin2? = null,
    ) : Result<Plugin>(code, "load plugin: $message", data)
}