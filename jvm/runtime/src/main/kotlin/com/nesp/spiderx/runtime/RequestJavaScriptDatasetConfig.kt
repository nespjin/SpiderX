package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
open class RequestJavaScriptDatasetConfig {

    fun shouldOverrideUrlLoading(url: String): Boolean? = null

    fun shouldInterceptRequest(url: String): String? = null

}