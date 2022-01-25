package com.nesp.fishplugin.runtime.js

import com.nesp.fishplugin.core.Environment

abstract class JsRuntimeInterface : IJsRuntimeInterface {

    override fun getApiLevel(): Int {
        return Environment.shared.getBuild().runtimeApiLevel
    }

    override fun getVersionCode(): Int {
        return Environment.shared.getBuild().runtimeVersionCode
    }

    override fun getVersionName(): String {
        return Environment.shared.getBuild().runtimeVersionName
    }

    override fun getBuild(): String {
        return Environment.shared.getBuild().runtimeBuild
    }

    override fun getDeviceType(): Int {
        return Environment.shared.getDeviceType()
    }

    override fun isMobilePhone(): Boolean {
        return Environment.shared.isMobilePhone()
    }

    override fun isTable(): Boolean {
        return Environment.shared.isTable()
    }

    override fun isDesktop(): Boolean {
        return Environment.shared.isDesktop()
    }

    override fun sendPage2Platform(page: String) {
    }

    override fun sendError2Platform(errorMsg: String) {
    }

    override fun printHtml(html: String) {

    }
}