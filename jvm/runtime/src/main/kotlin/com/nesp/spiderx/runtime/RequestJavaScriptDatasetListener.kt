package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
abstract class RequestJavaScriptDatasetListener : RequestDatasetListener() {

    fun onPageStarted(url: String) {}

    fun onPageCancelled(url: String) {}

    fun onPageFinished(url: String) {}

    fun onPageError(url: String, error: String) {}

    fun onLoadProgress(url: String, progress: Int) {}

}