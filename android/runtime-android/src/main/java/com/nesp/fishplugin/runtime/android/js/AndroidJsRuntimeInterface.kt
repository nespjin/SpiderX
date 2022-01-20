package com.nesp.fishplugin.runtime.android.js

import android.webkit.JavascriptInterface
import com.nesp.fishplugin.core.Environment
import com.nesp.fishplugin.runtime.js.IJsRuntimeInterface

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 1:18
 * Description:
 **/
open class AndroidJsRuntimeInterface : IJsRuntimeInterface {

    @JavascriptInterface
    override fun getApiLevel(): Int {
        return Environment.shared.getBuild().runtimeApiLevel
    }

    @JavascriptInterface
    override fun getVersionCode(): Int {
        return Environment.shared.getBuild().runtimeVersionCode
    }

    @JavascriptInterface
    override fun getVersionName(): String {
        return Environment.shared.getBuild().runtimeVersionName
    }

    @JavascriptInterface
    override fun getBuild(): String {
        return Environment.shared.getBuild().runtimeBuild
    }

    @JavascriptInterface
    override fun getDeviceType(): Int {
        return Environment.shared.getDeviceType()
    }

    @JavascriptInterface
    override fun isMobilePhone(): Boolean {
        return Environment.shared.isMobilePhone()
    }

    @JavascriptInterface
    override fun isTable(): Boolean {
        return Environment.shared.isTable()
    }

    @JavascriptInterface
    override fun isDesktop(): Boolean {
        return Environment.shared.isDesktop()
    }

    @JavascriptInterface
    override fun sendPage2Platform(page: String) {
    }

    @JavascriptInterface
    override fun sendError2Platform(errorMsg: String) {
    }

    @JavascriptInterface
    override fun printHtml(html: String) {

    }

    companion object {

        private const val TAG = "JsRuntimeInterface"

    }
}