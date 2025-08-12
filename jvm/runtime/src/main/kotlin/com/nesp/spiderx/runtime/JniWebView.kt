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
}