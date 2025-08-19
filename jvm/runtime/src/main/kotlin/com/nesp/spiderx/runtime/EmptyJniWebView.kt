package com.nesp.spiderx.runtime

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.concurrent.thread

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
open class EmptyJniWebView : JniWebView() {

    override fun onInit() {
        LOGGER.debug("onInit")
    }

    override fun performLoadUrl(url: String) {
        LOGGER.debug("performLoadUrl")
        thread {
            Thread.sleep(1000)
            postMainThread {
                notifyOnPageFinished(url)
            }
        }
    }

    override fun performLoadData(data: String) {
        LOGGER.debug("performLoadData")
    }

    override fun performReload() {
        LOGGER.debug("performReload")
    }

    override fun performEvaluate(javascript: String): String? {
        LOGGER.debug("performEvaluate")
        return null
    }

    override fun onDestroy() {
        LOGGER.debug("onDestroy")
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(EmptyJniWebView::class.java)
    }
}