package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
open class RequestJavaScriptDatasetConfig {

    open fun shouldOverrideUrlLoading(url: String): Boolean? = null

    open fun shouldInterceptRequest(url: String): String? = null

}