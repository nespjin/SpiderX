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
package com.nesp.spiderx.runtime.android

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.nesp.spiderx.runtime.JniWebView
import com.nesp.spiderx.runtime.PluginManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class AndroidWebView(private var context: Context?) : JniWebView() {

    private var webView: WebView? = null

    override fun onInit() {
        if (webView != null) return

        val pluginManager = PluginManager.instance

        webView = WebView(context!!).apply {
            visibility = WebView.INVISIBLE
            layoutParams = LinearLayout.LayoutParams(1, 1)
            clearFocus()
            settings.defaultTextEncodingName = "utf-8"
            settings.userAgentString = pluginManager.getUserAgent()
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.pluginState = WebSettings.PluginState.OFF
            settings.displayZoomControls = false
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.savePassword = false
            settings.saveFormData = false
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.mediaPlaybackRequiresUserGesture = false
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.loadsImagesAutomatically = false
            settings.blockNetworkImage = true
            settings.blockNetworkLoads = false
            settings.databaseEnabled = true

            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

            addJavascriptInterface(
                AndroidSpiderXRuntimeJavaScriptObject(this@AndroidWebView),
                SPIDERX_RUNTIME_JAVASCRIPT_OBJECT_NAME
            )

            enableCokie(this)

            webChromeClient = DefaultWebChromeClient(this@AndroidWebView)
            webViewClient = DefaultWebViewClient(this@AndroidWebView)
        }
    }

    override fun performLoadUrl(url: String) {
        webView?.loadUrl(url)
    }

    override fun performLoadData(data: String) {
        webView?.loadData(data, "text/html", "utf-8")
    }

    override fun performReload() {
        webView?.reload()
    }

    override fun performEvaluate(javascript: String): String? {
        val ret = CompletableFuture<String>()
        webView?.evaluateJavascript(javascript, ret::complete)
        return try {
            ret.get(500, TimeUnit.MILLISECONDS)
        } catch (_: Exception) {
            null
        }
    }

    override fun onDestroy() {
        context = null

        if (webView != null) {
            webView?.removeAllViews()
            webView?.settings?.javaScriptEnabled = false
            webView?.clearHistory()
            webView?.clearFormData()
            webView?.stopLoading()
            webView?.destroy()
            webView = null
        }
    }

    private fun enableCokie(webView: WebView) {
        val cookieManager = CookieManager.getInstance()
        CookieSyncManager.createInstance(context)
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
    }

    private class DefaultWebChromeClient(private val webView: AndroidWebView) : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            webView.notifyOnLoadProgress(newProgress)
        }

    }

    private class DefaultWebViewClient(private val webView: AndroidWebView) : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            webView.notifyOnPageStarted(url ?: "")
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            webView.notifyOnShouldInterceptRequest(request?.url?.toString() ?: "")
            return super.shouldInterceptRequest(view, request)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            webView.notifyOnPageError(
                request?.url?.toString() ?: "",
                error?.description?.toString() ?: ""
            )
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return webView.notifyOnShouldOverrideUrlLoading(request?.url?.toString() ?: "")
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val javascript = "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'"
            val document = webView.evaluate(javascript) ?: ""
            webView.notifyOnPageFinished(url ?: "", document)
        }
    }

    private class AndroidSpiderXRuntimeJavaScriptObject(private val webView: AndroidWebView) :
        SpiderXRuntimeJavaScriptObject {

        @JavascriptInterface
        override fun sendData(data: String) {
            val url: String = webView.webView?.url ?: ""
            webView.notifyOnReceivedData(url, data)
        }

        @JavascriptInterface
        override fun sendError(error: String) {
            val url: String = webView.webView?.url ?: ""
            webView.notifyOnReceivedError(url, error)
        }
    }

}