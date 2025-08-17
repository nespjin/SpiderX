package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
abstract class JniWebView {
    abstract fun init()

    abstract fun loadUrl(url: String)

    abstract fun loadData(data: String)

    abstract fun reload()

    abstract fun evaluate(javascript: String): String?

    abstract fun destroy()

    fun notifyOnPageStarted(url: String) {
        nativeNotifyOnPageStarted(url)
    }

    private external fun nativeNotifyOnPageStarted(url: String)

    fun notifyOnPageCancelled(url: String) {
        nativeNotifyOnPageCancelled(url)
    }

    private external fun nativeNotifyOnPageCancelled(url: String)

    fun notifyOnPageFinished(url: String) {
        nativeNotifyOnPageFinished(url)
    }

    private external fun nativeNotifyOnPageFinished(url: String)

    fun notifyOnPageError(url: String, error: String) {
        nativeNotifyOnPageError(url, error)
    }

    private external fun nativeNotifyOnPageError(url: String, error: String)

    fun notifyOnLoadProgress(progress: Int) {
        nativeNotifyOnLoadProgress(progress)
    }

    private external fun nativeNotifyOnLoadProgress(progress: Int)

    fun notifyOnShouldOverrideUrlLoading(url: String): Boolean {
        return nativeNotifyOnShouldOverrideUrlLoading(url)
    }

    private external fun nativeNotifyOnShouldOverrideUrlLoading(url: String): Boolean

    fun notifyOnShouldInterceptRequest(url: String): String? {
        return nativeNotifyOnShouldInterceptRequest(url)
    }

    private external fun nativeNotifyOnShouldInterceptRequest(url: String): String?

    fun notifyOnReceivedData(url: String, data: String) {
        nativeNotifyOnReceivedData(url, data)
    }

    private external fun nativeNotifyOnReceivedData(url: String, data: String)

    fun notifyOnReceivedError(url: String, error: String) {
        nativeNotifyOnReceivedError(url, error)
    }

    private external fun nativeNotifyOnReceivedError(url: String, error: String)

    interface SpiderXRuntimeJavaScriptObject {
        fun sendData(data: String)

        fun sendError(error: String)
    }

    companion object {
        const val SPIDERX_RUNTIME_JAVASCRIPT_OBJECT_NAME = "spiderxRT"
    }
}