package com.nesp.fishplugin.runtime.android.js

import android.content.Context
import android.os.Build
import android.view.View
import android.webkit.*
import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.core.data.Plugin
import com.nesp.fishplugin.runtime.Process
import com.nesp.fishplugin.runtime.js.IEventJsRuntimeInterface
import com.nesp.fishplugin.runtime.js.JsRuntime
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 1:33
 * Description:
 **/
class AndroidJsRuntime(
    context: Context
) : JsRuntime() {

    private val context = context.applicationContext

    private val androidJsRuntimeInterface = object : AndroidJsRuntimeInterface() {

        override fun sendData(type: Int, data: String) {
            super.sendData(type, data)
        }

        override fun sendError(errorMsg: String) {
            super.sendError(errorMsg)
        }
    }

    var eventJsRuntimeInterface: IEventJsRuntimeInterface? = null

    override fun exec(page: Page): Process {
        val process = Process()

        val webView = WebView(context)

        val environment = Environment.shared
        var userAgent =
            "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36"
        if (!environment.isMobilePhone()) {
            // Using PC
            userAgent =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36"
        }
        val settings = webView.settings
        settings.defaultTextEncodingName = "utf-8"
        settings.userAgentString = userAgent
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.pluginState = WebSettings.PluginState.OFF
        settings.displayZoomControls = true
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.savePassword = false
        settings.saveFormData = false
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.mediaPlaybackRequiresUserGesture = true
        }
        if (Build.VERSION.SDK_INT > 16) {
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
        }
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.loadsImagesAutomatically = false
        settings.blockNetworkImage = true
        settings.blockNetworkLoads = false
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(context.cacheDir.absolutePath)
        settings.databaseEnabled = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webView.addJavascriptInterface(androidJsRuntimeInterface, "runtime")
        enableWebViewCookie(webView)


        val url = page.url
        val realUrl = Plugin.removeReqPrefix(url)

        if (Plugin.isPostReq(url)) {
            // Post
            val urlObj = URL(realUrl)
            val query = if (urlObj.query != null) {
                val data: MutableMap<String, String> = hashMapOf()
                val kvPairs = urlObj.query.split("&")
                for (kvPair in kvPairs) {
                    val kvArray = kvPair.split("=")
                    data[kvArray[0]] = kvArray[1]
                }
                urlObj.query
            } else ""
            webView.postUrl(realUrl, query.toByteArray(StandardCharsets.UTF_8))
        } else {
            // Get
            webView.loadUrl(realUrl)
        }
        return process
    }

    private fun enableWebViewCookie(webView: WebView) {
        val instance: CookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(context)
        }
        instance.setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= 21) {
            instance.setAcceptThirdPartyCookies(webView, true)
        }
    }

    override fun runTask(task: Any) {
        TODO("Not yet implemented")
    }

    override fun interruptCurrentTask() {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun shutdownNow() {
        TODO("Not yet implemented")
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit) {
        TODO("Not yet implemented")
    }

    override fun isTerminated(): Boolean {
        TODO("Not yet implemented")
    }

    companion object {

        private const val TAG = "AndroidJsRuntime"

    }
}