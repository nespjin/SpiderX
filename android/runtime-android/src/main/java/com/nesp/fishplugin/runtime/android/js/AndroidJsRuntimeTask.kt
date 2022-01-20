package com.nesp.fishplugin.runtime.android.js

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.CancellationSignal
import android.os.OperationCanceledException
import android.os.SystemClock
import android.view.View
import android.webkit.*
import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.core.data.Plugin
import com.nesp.fishplugin.runtime.IJsRuntimeTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 11:42
 * Description:
 **/
abstract class AndroidJsRuntimeTask(context: Context) : IJsRuntimeTask {

    private val context = context.applicationContext

    var listener: AndroidJsRuntimeTaskListener? = null
    private val coroutineScope = CoroutineScope(EmptyCoroutineContext)

    var isLoading = false
        private set
    var isLoadFinished = false
        private set(value) {
            field = value
            if (value) {
                loadTimer?.cancel()
            }
        }

    private var timeoutWatcherTimer: Timer? = null
    private var loadTimer: Timer? = null
    var js: String = ""
    var timeout = 60 * 1000L

    abstract fun run(webView: WebView)

    fun run() {
        if (webView == null) {
            webView = WebView(context)
            initWebView(webView!!)
        }
        run(webView!!)
    }

    private var webView: WebView? = null

    private val androidJsRuntimeInterface = object : AndroidJsRuntimeInterface() {

        override fun sendPage2Platform(page: String) {
            listener?.onReceivePage(page)
        }

        override fun sendError2Platform(errorMsg: String) {
            listener?.onReceiveError(errorMsg)
        }

        override fun printHtml(html: String) {
            listener?.onPrintHtml(html)
        }
    }

    override fun interrupt() {
        destroy()
    }

    fun isRunning(): Boolean {
        return isLoading
    }

    @Synchronized
    fun awaitFinish() {
        while (isLoading) {
            SystemClock.sleep(50)
        }
    }

    /**
     * @throws OperationCanceledException if the operation has been canceled.
     */
    @Throws(OperationCanceledException::class)
    fun awaitFinish(cancellationSignal: CancellationSignal) {
        while (isLoading) {
            cancellationSignal.throwIfCanceled()
            SystemClock.sleep(50)
        }
    }

    fun pauseTimers() {
        webView?.pauseTimers()
    }

    fun resumeTimers() {
        webView?.resumeTimers()
    }

    fun pause() {
        webView?.onPause()
    }

    fun resume() {
        webView?.onResume()
    }

    fun destroy() {
        coroutineScope.cancel()
        timeoutWatcherTimer?.cancel()
        timeoutWatcherTimer = null
        loadTimer?.cancel()
        loadTimer = null

        if (webView == null) return
        webView!!.removeAllViews()
        webView!!.removeAllViewsInLayout()
        webView!!.settings.javaScriptEnabled = false
        webView!!.clearHistory()
        webView!!.clearFormData()
        webView!!.stopLoading()
        webView!!.destroy()
        webView = null
    }

    fun execCurrentJs() {
        prepareJsRuntime()
        execJs(js)

        execJsRuntimeInitialize()
        val execJsRuntimeLoadPage = execJsRuntimeLoadPage()
        if (execJsRuntimeLoadPage != null && execJsRuntimeLoadPage is String) {
            listener?.onReceivePage(execJsRuntimeLoadPage)
        }
    }

    @Synchronized
    override fun execJs(js: String): Any? {
        var js2 = js.replace("\n".toRegex(), "").trim { it <= ' ' }
        js2 = "javascript:$js2"

        var result: String? = ""
        webView?.evaluateJavascript(js2) { result = it }

        // wait result
        while (result?.isEmpty() == true) {
            try {
                Thread.sleep(50)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }

        //        if (result?.isEmpty() == true) {
        //            webView?.loadUrl(js2)
        //        }

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
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(context.cacheDir.absolutePath)
        settings.databaseEnabled = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webView.addJavascriptInterface(androidJsRuntimeInterface, "runtime")
        enableWebViewCookie(webView)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    if (!isLoadFinished) execCurrentJs()
                    isLoading = false
                    isLoadFinished = true
                } else {
                    isLoading = true
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
                error: SslError?
            ) {
                handler?.proceed()
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
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
                isLoadFinished = true
                listener?.onPageStart()

                loadTimer?.cancel()
                loadTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (!isLoadFinished) {
                            coroutineScope.launch(Dispatchers.Main) { execCurrentJs() }
                            isLoadFinished = true
                            isLoading = false
                            listener?.onPageFinished()
                        }

                        loadTimer?.cancel()
                        loadTimer = null

                        timeoutWatcherTimer?.cancel()
                        timeoutWatcherTimer = null
                    }

                }, 10 * 1000, 1)

                // timeoutWatcherTimer is not perform anyway
                timeoutWatcherTimer?.cancel()
                timeoutWatcherTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (isLoadFinished) return
                        listener?.onTimeout()
                        timeoutWatcherTimer?.cancel()
                        timeoutWatcherTimer = null
                    }
                }, timeout, 1)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                listener?.onPageFinished()
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

    companion object {

        private const val TAG = "AndroidJsRuntimeTask"

    }
}