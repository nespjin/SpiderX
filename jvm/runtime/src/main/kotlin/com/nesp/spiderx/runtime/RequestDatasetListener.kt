package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
abstract class RequestDatasetListener {

    open fun onReceivedData(url: String, data: String) {}

    open fun onReceivedError(url: String, error: String) {}
}
