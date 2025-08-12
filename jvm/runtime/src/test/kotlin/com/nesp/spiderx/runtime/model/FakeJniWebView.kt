package com.nesp.spiderx.runtime.model

import com.nesp.spiderx.runtime.JniWebView
import kotlin.concurrent.thread

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class FakeJniWebView : JniWebView() {
    private var mPtr = -1L

    override fun init() {
        println("FakeJniWebView >>> init")
    }

    override fun loadUrl(url: String) {
        println("FakeJniWebView >>> loadUrl $url")
        notifyOnPageStarted(url)
        for (i in 0..100) {
            notifyOnLoadProgress(i)
            Thread.sleep(10)
        }
        notifyOnPageError(url, "Error on Finished")
        val shouldInterceptRequest = notifyOnShouldInterceptRequest(url)
        println("shouldInterceptRequest: $shouldInterceptRequest")
        val shouldOverrideUrlLoading = notifyOnShouldOverrideUrlLoading(url)
        println("shouldOverrideUrlLoading: $shouldOverrideUrlLoading")
        notifyOnPageFinished(url)
    }

    override fun loadData(data: String) {
        println("FakeJniWebView >>> loadData $data")
    }

    override fun reload() {
        println("FakeJniWebView >>> reload")
    }

    override fun evaluate(javascript: String): String? {
        println("FakeJniWebView >>> evaluate $javascript")
        return "evaluate $javascript"
    }

    override fun destroy() {
        println("FakeJniWebView >>> destroy")
    }

}