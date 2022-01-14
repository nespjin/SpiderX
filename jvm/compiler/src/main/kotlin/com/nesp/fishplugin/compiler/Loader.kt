package com.nesp.fishplugin.compiler

import com.google.gson.Gson
import com.nesp.fishplugin.core.data.Plugin
import okhttp3.OkHttpClient
import okhttp3.Request
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
     * Load plugin from disk
     */
    fun loadFromDisk(path: String): LoadResult {
        val plugin = loadPluginFromDisk(path)
            ?: return LoadResult(LoadResult.STATE_FAILED, "Failed to load plugin from $path")

        return load(plugin)
    }

    /**
     * Load plugin from url
     */
    fun loadFromUrl(url: String): LoadResult {
        val plugin = loadPluginFromUrl(url)
            ?: return LoadResult(LoadResult.STATE_FAILED, "Failed to load plugin from $url")
        return load(plugin)
    }

    /**
     * Load the plugin
     */
    fun load(plugin: Plugin): LoadResult {
        if (plugin.parent != null) {
            // Handle parent plugin
            val parent = plugin.parent!!
            if (parent is String) {
                // load parent plugin from ref path.
                plugin.parent = if (parent.startsWith("http://", true)
                    || parent.startsWith("https://", true)
                ) {
                    loadPluginFromUrl(parent) ?: return LoadResult(
                        LoadResult.STATE_FAILED,
                        "load parent plugin from url($parent) failed."
                    )
                } else {
                    loadPluginFromDisk(parent) ?: return LoadResult(
                        LoadResult.STATE_FAILED,
                        "load parent plugin from disk path($parent) failed."
                    )
                }
            }
        }

        return LoadResult(LoadResult.STATE_SUCCESS, plugin = plugin)
    }

    private fun loadPluginFromDisk(path: String): Plugin? {
        val pluginFile = File(path)
        if (!pluginFile.exists() || !pluginFile.isFile) return null
        val pluginString = String(pluginFile.readBytes(), StandardCharsets.UTF_8)
        return Gson().fromJson(pluginString, Plugin::class.java)
    }

    private fun loadPluginFromUrl(url: String): Plugin? {
        val request = Request.Builder().get().url(url).build()
        val response = httpClient.newCall(request).execute()
        val code = response.code
        val responseBody = response.body ?: return null
        if (code != 400) return null
        return Gson().fromJson(responseBody.string(), Plugin::class.java)
    }

    data class LoadResult(
        var state: Int,
        var message: String = "",
        var plugin: Plugin? = null
    ) {
        companion object {
            const val STATE_FAILED = 1
            const val STATE_SUCCESS = 0
        }
    }
}