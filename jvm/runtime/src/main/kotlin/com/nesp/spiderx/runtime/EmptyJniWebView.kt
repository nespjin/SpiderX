package com.nesp.spiderx.runtime

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class EmptyJniWebView : JniWebView() {

    override fun onInit() {
        LOGGER.debug("EmptyJniWebView onInit")
    }

    override fun performLoadUrl(url: String) {
        LOGGER.debug("EmptyJniWebView performLoadUrl")
    }

    override fun performLoadData(data: String) {
        LOGGER.debug("EmptyJniWebView performLoadData")
    }

    override fun performReload() {
        LOGGER.debug("EmptyJniWebView performReload")
    }

    override fun performEvaluate(javascript: String): String? {
        LOGGER.debug("EmptyJniWebView performEvaluate")
        return null
    }

    override fun onDestroy() {
        LOGGER.debug("EmptyJniWebView onDestroy")
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(EmptyJniWebView::class.java)
    }
}