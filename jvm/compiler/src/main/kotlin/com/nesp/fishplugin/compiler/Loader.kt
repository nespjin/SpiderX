package com.nesp.fishplugin.compiler

import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.Result
import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.core.data.Plugin
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

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
    fun load(plugin: Plugin): LoadResult {
        if (!Environment.shared.isSupport(plugin)) {
            return LoadResult(Result.CODE_FAILED, "Plugin is not support current device type")
        }
        return LoadResult(Result.CODE_SUCCESS, data = plugin)
    }

    /**
     * Load plugin from disk
     */
    fun loadPluginFromDisk(path: String): LoadResult {
        val pluginFile = File(path)
        if (!pluginFile.exists() || !pluginFile.isFile)
            return LoadResult(Result.CODE_FAILED, "Load plugin failed from path:$path")
        val pluginString = String(pluginFile.readBytes(), StandardCharsets.UTF_8)
        val loadPluginFromJsonString = loadPluginFromJsonString(pluginString)
        if (loadPluginFromJsonString.data != null) {
            return load(loadPluginFromJsonString.data!!)
        }
        return loadPluginFromJsonString
    }

    /**
     * Load plugin from url
     */
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
        val loadPluginFromJsonString = loadPluginFromJsonString(responseBody.string())
        if (loadPluginFromJsonString.data != null) {
            return load(loadPluginFromJsonString.data!!)
        }
        return loadPluginFromJsonString
    }

    /**
     * Create instance of plugin and pick the correct field value according to device type.
     */
    private fun loadPluginFromJsonString(jsonString: String): LoadResult {
        if (jsonString.isEmpty()) return LoadResult(Result.CODE_FAILED, "The json is empty")
        try {
            val jsonPluginRoot = JSONObject(jsonString)
            val deviceType = Environment.shared.getDeviceType()

            val plugin = Plugin()

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
                    plugin.parent = if (parent.startsWith("http://", true)
                        || parent.startsWith("https://", true)
                    ) {
                        loadPluginFromUrl(parent)
                    } else {
                        loadPluginFromDisk(parent)
                    }
                }
            }

            // Name
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_NAME}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_NAME}-$deviceType")
            ) {
                (jsonPluginRoot.get("${Plugin.FILED_NAME_NAME}-$deviceType")?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.name = it }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_NAME)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_NAME)
            ) {
                (jsonPluginRoot.get(Plugin.FILED_NAME_NAME)?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.name = it }
            }

            // Version
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_VERSION}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_VERSION}-$deviceType")
            ) {
                (jsonPluginRoot.get("${Plugin.FILED_NAME_VERSION}-$deviceType")?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.version = it }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_VERSION)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_VERSION)
            ) {
                (jsonPluginRoot.get(Plugin.FILED_NAME_VERSION)?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.version = it }
            }

            // Runtime
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_RUNTIME}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_RUNTIME}-$deviceType")
            ) {
                (jsonPluginRoot.get("${Plugin.FILED_NAME_RUNTIME}-$deviceType")?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.runtime = it }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_RUNTIME)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_RUNTIME)
            ) {
                (jsonPluginRoot.get(Plugin.FILED_NAME_RUNTIME)?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.runtime = it }
            }

            // Time
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_TIME}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_TIME}-$deviceType")
            ) {
                (jsonPluginRoot.get("${Plugin.FILED_NAME_TIME}-$deviceType")?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.time = it }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_TIME)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_TIME)
            ) {
                (jsonPluginRoot.get(Plugin.FILED_NAME_TIME)?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.time = it }
            }

            // Tags
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_TAGS}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_TAGS}-$deviceType")
            ) {
                val tagsJsonNode = jsonPluginRoot.get("${Plugin.FILED_NAME_TAGS}-$deviceType")
                if (tagsJsonNode is JSONArray) {
                    val tags = arrayListOf<String>()
                    for (i in 0 until tagsJsonNode.length()) {
                        tagsJsonNode.optString(i, "").also { if (it.isNotEmpty()) tags.add(it) }
                    }
                    plugin.tags = tags
                }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_TAGS)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_TAGS)
            ) {
                val tagsJsonNode = jsonPluginRoot.get(Plugin.FILED_NAME_TAGS)
                if (tagsJsonNode is JSONArray) {
                    val tags = arrayListOf<String>()
                    for (i in 0 until tagsJsonNode.length()) {
                        tagsJsonNode.optString(i, "").also { if (it.isNotEmpty()) tags.add(it) }
                    }
                    plugin.tags = tags
                }
            }

            // Device Flags
            // Type

            // Introduction
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_INTRODUCTION}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_INTRODUCTION}-$deviceType")
            ) {
                (jsonPluginRoot.get("${Plugin.FILED_NAME_INTRODUCTION}-$deviceType")?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.introduction = it }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_INTRODUCTION)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_INTRODUCTION)
            ) {
                (jsonPluginRoot.get(Plugin.FILED_NAME_INTRODUCTION)?.toString()
                    ?: "").also { if (it.isNotEmpty()) plugin.introduction = it }
            }

            // Ref
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_REF}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_REF}-$deviceType")
            ) {
                jsonPluginRoot.optJSONObject("${Plugin.FILED_NAME_REF}-$deviceType")?.also {
                    plugin.ref = it.toMap()
                }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_REF)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_REF)
            ) {
                jsonPluginRoot.optJSONObject(Plugin.FILED_NAME_REF)?.also {
                    plugin.ref = it.toMap()
                }
            }

            // Pages
            if (jsonPluginRoot.has("${Plugin.FILED_NAME_PAGES}-$deviceType")
                && !jsonPluginRoot.isNull("${Plugin.FILED_NAME_PAGES}-$deviceType")
            ) {
                jsonPluginRoot.optJSONArray("${Plugin.FILED_NAME_PAGES}-$deviceType")?.also {
                    val pages = arrayListOf<Page>()
                    for (i in 0 until it.length()) {
                        val loadPageFromJsonObject = loadPageFromJsonObject(it.optJSONObject(i))
                        if (loadPageFromJsonObject.code != Result.CODE_SUCCESS) {
                            return LoadResult(Result.CODE_FAILED, loadPageFromJsonObject.message)
                        }
                        pages.add(loadPageFromJsonObject.data!!)
                    }
                    plugin.pages = pages
                }
            } else if (jsonPluginRoot.has(Plugin.FILED_NAME_PAGES)
                && !jsonPluginRoot.isNull(Plugin.FILED_NAME_PAGES)
            ) {
                jsonPluginRoot.optJSONArray(Plugin.FILED_NAME_PAGES)?.also {
                    val pages = arrayListOf<Page>()
                    for (i in 0 until it.length()) {
                        val loadPageFromJsonObject = loadPageFromJsonObject(it.optJSONObject(i))
                        if (loadPageFromJsonObject.code != Result.CODE_SUCCESS) {
                            return LoadResult(Result.CODE_FAILED, loadPageFromJsonObject.message)
                        }
                        pages.add(loadPageFromJsonObject.data!!)
                    }
                    plugin.pages = pages
                }
            }

            return LoadResult(Result.CODE_SUCCESS, data = plugin)
        } catch (e: JSONException) {
            return LoadResult(Result.CODE_FAILED, "error occurs when parsing plugin")
        }
    }

    private fun loadPageFromJsonObject(jsonPageRoot: JSONObject?): LoadPageResult {
        if (jsonPageRoot == null) {
            return LoadPageResult(Result.CODE_FAILED, "Json object is null.")
        }

        val deviceType = Environment.shared.getDeviceType()
        val page = Page()

        // RefUrl
        if (jsonPageRoot.has("${Page.FIELD_NAME_REF_URL}-$deviceType")
            && !jsonPageRoot.isNull("${Page.FIELD_NAME_REF_URL}-$deviceType")
        ) {
            (jsonPageRoot.get("${Page.FIELD_NAME_REF_URL}-$deviceType")?.toString()
                ?: "").also { if (it.isNotEmpty()) page.refUrl = it }
        } else if (jsonPageRoot.has(Page.FIELD_NAME_REF_URL)
            && !jsonPageRoot.isNull(Page.FIELD_NAME_REF_URL)
        ) {
            (jsonPageRoot.get(Page.FIELD_NAME_REF_URL)?.toString()
                ?: "").also { if (it.isNotEmpty()) page.refUrl = it }
        }

        if (page.refUrl.trim().isNotEmpty()) {
            return loadPageFromUrl(page.refUrl)
        }

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
            page.dsl = jsonPageRoot.get("${Page.FIELD_NAME_DSL}-$deviceType")
        } else if (jsonPageRoot.has(Page.FIELD_NAME_DSL)
            && !jsonPageRoot.isNull(Page.FIELD_NAME_DSL)
        ) {
            page.dsl = jsonPageRoot.get(Page.FIELD_NAME_DSL)
        }

        return LoadPageResult(Result.CODE_SUCCESS, data = page)
    }

    /**
     * Load page from url
     */
    fun loadPageFromUrl(url: String): LoadPageResult {
        val request = Request.Builder().get().url(url).build()
        val response = httpClient.newCall(request).execute()
        val code = response.code
        val responseBody =
            response.body ?: return LoadPageResult(
                Result.CODE_FAILED,
                "Load page failed from url:$url"
            )
        if (code != 400) return LoadPageResult(Result.CODE_FAILED, "Load page failed from url:$url")
        return loadPageFromJsonObject(JSONObject(responseBody.string()))
    }

    class LoadPageResult(
        code: Int = CODE_FAILED,
        message: String = "",
        data: Page? = null
    ) : Result<Page>(code, message, data)

    class LoadResult(
        code: Int = CODE_FAILED,
        message: String = "",
        data: Plugin? = null
    ) : Result<Plugin>(code, message, data)
}