package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
abstract class RequestDatasetListener {

    fun onReceivedData(url: String, data: String) {}

    fun onReceivedError(url: String, error: String) {}
}
