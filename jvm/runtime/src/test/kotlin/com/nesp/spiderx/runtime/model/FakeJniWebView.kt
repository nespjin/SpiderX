package com.nesp.spiderx.runtime.model

import com.nesp.spiderx.runtime.JniWebView
import com.nesp.spiderx.runtime.utils.Looper
import org.apache.logging.log4j.LogManager
import java.util.concurrent.Future

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class FakeJniWebView : JniWebView() {

    override fun onInit() {
        LOGGER.trace("FakeJniWebView >>> onInit")
    }

    override fun performLoadUrl(url: String) {
        postBackgroundThread {
            LOGGER.trace("FakeJniWebView >>> performLoadUrl $url")
            notifyOnPageStarted(url)
            for (i in 0..100) {
                notifyOnLoadProgress(i)
                Thread.sleep(10)
            }
//        notifyOnPageError(url, "Error on Finished")
            val shouldInterceptRequest = notifyOnShouldInterceptRequest(url)
            LOGGER.trace("shouldInterceptRequest: $shouldInterceptRequest")
            val shouldOverrideUrlLoading = notifyOnShouldOverrideUrlLoading(url)
            LOGGER.trace("shouldOverrideUrlLoading: $shouldOverrideUrlLoading")
            notifyOnPageFinished(url)
        }
    }

    override fun performLoadData(data: String) {
        LOGGER.trace("FakeJniWebView >>> performLoadData $data")
    }

    override fun performReload() {
        LOGGER.trace("FakeJniWebView >>> performReload")
    }

    override fun performEvaluate(javascript: String): String? {
        LOGGER.trace("FakeJniWebView >>> performEvaluate $javascript")
        return "performEvaluate $javascript"
    }

    override fun onDestroy() {
        LOGGER.trace("FakeJniWebView >>> onDestroy")
        postBackgroundThread { Looper.main.finish() }
    }

    override fun ensureRunOnBackgroundThread() {

    }

    override fun postMainThread(task: Runnable): Future<*> {
        return super.postMainThread({
            LOGGER.trace("postMainThread ${Thread.currentThread().name}")
            task.run()
        })
    }

    override fun dispatchMainThread(task: Runnable) {
        Looper.main.post(task)
    }

    companion object {
        private val LOGGER = LogManager.getLogger(FakeJniWebView::class.java)
    }

}