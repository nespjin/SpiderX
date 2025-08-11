package com.nesp.spiderx.runtime.model

import com.nesp.spiderx.runtime.JniWebView

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class FakeJniWebView : JniWebView() {
    override fun init() {
        println("FakeJniWebView >>> init")
    }

    override fun loadUrl(url: String) {
        println("FakeJniWebView >>> loadUrl $url")
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