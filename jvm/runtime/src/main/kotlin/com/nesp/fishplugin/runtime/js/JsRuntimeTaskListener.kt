package com.nesp.fishplugin.runtime.js

abstract class JsRuntimeTaskListener {

    open fun onPageLoadStart() {

    }

    open fun onShouldInterceptRequest(url: String) {

    }

    open fun onPageLoadFinished() {

    }

    open fun onReceiveError(error: String) {

    }

    open fun onReceivePage(page: String) {

    }

    open fun onTimeout() {

    }

    open fun onPrintHtml(html: String) {

    }

}