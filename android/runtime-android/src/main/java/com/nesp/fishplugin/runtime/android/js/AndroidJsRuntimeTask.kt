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

package com.nesp.fishplugin.runtime.android.js

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.runtime.CancellationSignal
import com.nesp.fishplugin.runtime.OperationCanceledException
import com.nesp.fishplugin.runtime.js.JsRuntimeTask
import com.nesp.fishplugin.tools.code.JsMinifier
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 11:42
 * Description:
 **/
abstract class AndroidJsRuntimeTask(context: Context) : JsRuntimeTask<WebView>() {

    private val context = context.applicationContext

    var listener: AndroidJsRuntimeTaskListener? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    var isLoading = false
        @Synchronized
        private set
    var isLoadFinished = false
        private set

    private val isReceivePageOrError = AtomicBoolean(false)

    fun setReceivePageOrError(receivePageOrError: Boolean) {
        isReceivePageOrError.set(receivePageOrError)
    }

    fun isReceivePageOrError(): Boolean {
        return isReceivePageOrError.get()
    }

    private var timeoutWatcherTimer: Timer? = null
    private var loadTimer: Timer? = null
    var js: String = ""
    var timeout = 60 * 1000L
    private var webView: WebView? = null

    override fun run() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException("AndroidJsRuntimeTask can't run on main thread")
        }
        val countDownLatch = CountDownLatch(1)
        coroutineScope.launch(Dispatchers.Main) {
            if (webView == null) {
                webView = WebView(context)
                initWebView(webView!!)
            }
            run(webView!!)
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }

    private val androidJsRuntimeInterface = object : AndroidJsRuntimeInterface() {

        override fun sendPage2Platform(page: String) {
            setReceivePageOrError(true)
            listener?.onReceivePage(page)
            cancelTimer(timeoutWatcherTimer)
            timeoutWatcherTimer = null
        }

        override fun sendError2Platform(errorMsg: String) {
            setReceivePageOrError(true)
            cancelTimer(timeoutWatcherTimer)
            timeoutWatcherTimer = null
            listener?.onReceiveError(errorMsg)
        }

        override fun printHtml(html: String) {
            Log.i(TAG, "printHtml: $html")
            listener?.onPrintHtml(html)
        }
    }

    override fun interrupt() {
        destroy()
    }

    @Synchronized
    override fun isRunning(): Boolean {
        return isLoading
    }

    @Synchronized
    override fun awaitFinish() {
        while (isLoading) {
            SystemClock.sleep(50)
        }
    }

    /**
     * @throws OperationCanceledException if the operation has been canceled.
     */
    @Throws(OperationCanceledException::class)
    override fun awaitFinish(cancellationSignal: CancellationSignal) {
        while (isLoading) {
            cancellationSignal.throwIfCanceled()
            SystemClock.sleep(50)
        }
    }

    override fun pauseTimers() {
        webView?.pauseTimers()
    }

    override fun resumeTimers() {
        webView?.resumeTimers()
    }

    override fun pause() {
        webView?.onPause()
    }

    override fun resume() {
        webView?.onResume()
    }

    override fun destroy() {
        coroutineScope.launch(Dispatchers.Main) {
            coroutineScope.cancel()
            timeoutWatcherTimer?.cancel()
            timeoutWatcherTimer = null
            loadTimer?.cancel()
            loadTimer = null

            if (webView == null) return@launch
            if (webView!!.parent != null) {
                (webView!!.parent as ViewGroup).removeView(webView!!)
            }
            webView!!.removeAllViews()
            webView!!.removeAllViewsInLayout()
            webView!!.settings.javaScriptEnabled = false
            webView!!.clearHistory()
            webView!!.clearFormData()
            webView?.stopLoading()
            webView!!.destroy()
            webView = null
        }
    }

    private var isExecCurrentJs = false

    override fun execCurrentJs() {
        coroutineScope.launch(Dispatchers.IO) {
            synchronized(this@AndroidJsRuntimeTask) {
                if (isExecCurrentJs) return@launch
                isExecCurrentJs = true
            }
            prepareJsRuntime()
            execJs(js)

            try {
                execJsRuntimeInitialize()
                val result = execJsRuntimeLoadPage()
                withContext(Dispatchers.Main) {
                    cancelTimer(timeoutWatcherTimer)
                    setReceivePageOrError(true)
                    if (DEBUG) {
                        Log.d(TAG, "execCurrentJs: result=$result")
                    }
                    if (result != null && result is String) {
                        if (result.isEmpty()) {
                            listener?.onReceiveError("Load Page Failed")
                        } else {
                            listener?.onReceivePage(result)
                        }
                    } else {
                        listener?.onReceiveError("Load Page Failed")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    cancelTimer(timeoutWatcherTimer)
                    setReceivePageOrError(true)
                    listener?.onReceiveError("Load Page Failed $e")
                }
            }
            synchronized(this@AndroidJsRuntimeTask) {
                isExecCurrentJs = false
            }
        }
    }

    @Synchronized
    override fun execJs(js: String): Any? {

        if (DEBUG) {
            Log.d(TAG, "[${Thread.currentThread().name}]execJs with $webView on $this : js = $js ")
        }

        var jsTmp = JsMinifier().minify(js).trim()
        jsTmp = "javascript:$jsTmp"

        var result: String? = ""
        val countDownLatch = CountDownLatch(1)
        coroutineScope.launch(Dispatchers.Main) {
            webView?.evaluateJavascript(jsTmp) {
                result = it
                if (result == "null") result = ""
                countDownLatch.countDown()
            }
        }

        if (DEBUG) {
            Log.d(TAG, "[${Thread.currentThread().name}]execJs start waite ret ")
        }

        // wait result
        try {
            countDownLatch.await(3, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        if (DEBUG) {
            Log.d(TAG, "[${Thread.currentThread().name}]execJs end waite ret")
        }

        return result
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(webView: WebView) {
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
        settings.setSupportMultipleWindows(false)
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
        // settings.setAppCacheEnabled(true)
        // settings.setAppCachePath(context.cacheDir.absolutePath)
        settings.databaseEnabled = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webView.addJavascriptInterface(androidJsRuntimeInterface, "runtimeNative")
        enableWebViewCookie(webView)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    coroutineScope.launch(Dispatchers.IO) {
                        synchronized(this@AndroidJsRuntimeTask) {
                            if (!isLoadFinished) {
                                isLoadFinished = true
                                isLoading = false
                                listener?.onPageLoadFinished()
                                execCurrentJs()
                            }
                        }
                    }
                } else {
                    isLoading = true
                    isLoadFinished = false
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?)
                    : Boolean {
                val url = request?.url?.toString() ?: return true
                return shouldOverrideUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return true
                if (!url.startsWith("http") || !url.startsWith("https")
                    || url.contains(".apk") || url.contains("sl.sogou")
                ) return true
                view!!.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(
                view: WebView?, handler: SslErrorHandler?,
                error: SslError?,
            ) {
                handler?.proceed()
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?,
            ): WebResourceResponse? {
                val url = request?.url?.toString() ?: return null
                return shouldInterceptRequest(view, url)
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?)
                    : WebResourceResponse? {
                if (!url.isNullOrEmpty()) return super.shouldInterceptRequest(view, url)
                listener?.onShouldInterceptRequest(url!!)
                return super.shouldInterceptRequest(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                isLoading = true
                isLoadFinished = false
                listener?.onPageLoadStart()

                cancelTimer(loadTimer)
                loadTimer = Timer()
                loadTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        synchronized(this@AndroidJsRuntimeTask) {
                            if (!isLoadFinished) {
                                isLoadFinished = true
                                isLoading = false
                                listener?.onPageLoadFinished()
                                execCurrentJs()
                            }

                            cancelTimer(loadTimer)
                            loadTimer = null
                        }
                    }
                }, 10 * 1000, 1)

                // timeoutWatcherTimer is not perform anyway
                cancelTimer(timeoutWatcherTimer)
                timeoutWatcherTimer = Timer()
                timeoutWatcherTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (isReceivePageOrError()) {
                            cancelTimer(timeoutWatcherTimer)
                            timeoutWatcherTimer = null
                            return
                        }
                        listener?.onTimeout()
                        cancelTimer(timeoutWatcherTimer)
                        timeoutWatcherTimer = null
                    }
                }, timeout, 1)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                listener?.onPageLoadFinished()
            }

        }
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

    private fun cancelTimer(timeoutWatcherTimer: Timer?) {
        timeoutWatcherTimer?.cancel()
    }

    companion object {
        private const val TAG = "AndroidJsRuntimeTask"
        private const val DEBUG = true
    }

}