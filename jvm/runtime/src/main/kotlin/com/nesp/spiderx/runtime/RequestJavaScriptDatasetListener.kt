package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
abstract class RequestJavaScriptDatasetListener : RequestDatasetListener() {

    open fun onPageStarted(url: String) {}

    open fun onPageCancelled(url: String) {}

    open fun onPageFinished(url: String, document: String) {}

    open fun onPageError(url: String, error: String) {}

    open fun onLoadProgress(url: String, progress: Int) {}

    open fun onShouldOverrideUrlLoading(url: String) {}

    open fun onShouldInterceptRequest(url: String) {}

}