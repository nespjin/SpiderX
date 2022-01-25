package com.nesp.fishplugin.runtime.android.js

import android.webkit.JavascriptInterface
import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.runtime.js.IJsRuntimeInterface
import com.nesp.fishplugin.runtime.js.JsRuntimeInterface

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 1:18
 * Description:
 **/
open class AndroidJsRuntimeInterface : JsRuntimeInterface() {

    @JavascriptInterface
    override fun getApiLevel(): Int {
        return super.getApiLevel()
    }

    @JavascriptInterface
    override fun getVersionCode(): Int {
        return super.getVersionCode()
    }

    @JavascriptInterface
    override fun getVersionName(): String {
        return super.getVersionName()
    }

    @JavascriptInterface
    override fun getBuild(): String {
        return super.getBuild()
    }

    @JavascriptInterface
    override fun getDeviceType(): Int {
        return super.getDeviceType()
    }

    @JavascriptInterface
    override fun isMobilePhone(): Boolean {
        return super.isMobilePhone()
    }

    @JavascriptInterface
    override fun isTable(): Boolean {
        return super.isTable()
    }

    @JavascriptInterface
    override fun isDesktop(): Boolean {
        return super.isDesktop()
    }

    @JavascriptInterface
    override fun sendPage2Platform(page: String) {
        super.sendPage2Platform(page)
    }

    @JavascriptInterface
    override fun sendError2Platform(errorMsg: String) {
        super.sendError2Platform(errorMsg)
    }

    @JavascriptInterface
    override fun printHtml(html: String) {
        super.printHtml(html)
    }
}