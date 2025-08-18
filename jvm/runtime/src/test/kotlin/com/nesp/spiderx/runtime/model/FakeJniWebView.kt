package com.nesp.spiderx.runtime.model

import com.nesp.spiderx.runtime.JniWebView
import com.nesp.spiderx.runtime.utils.Looper
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class FakeJniWebView : JniWebView() {

    override fun onInit() {
        println("FakeJniWebView >>> onInit")
    }

    override fun performLoadUrl(url: String) {
        postBackgroundThread {
            println("FakeJniWebView >>> performLoadUrl $url")
            notifyOnPageStarted(url)
            for (i in 0..100) {
                notifyOnLoadProgress(i)
                Thread.sleep(10)
            }
//        notifyOnPageError(url, "Error on Finished")
            val shouldInterceptRequest = notifyOnShouldInterceptRequest(url)
            println("shouldInterceptRequest: $shouldInterceptRequest")
            val shouldOverrideUrlLoading = notifyOnShouldOverrideUrlLoading(url)
            println("shouldOverrideUrlLoading: $shouldOverrideUrlLoading")
            notifyOnPageFinished(url)
        }
    }

    override fun performLoadData(data: String) {
        println("FakeJniWebView >>> performLoadData $data")
    }

    override fun performReload() {
        println("FakeJniWebView >>> performReload")
    }

    override fun performEvaluate(javascript: String): String? {
        println("FakeJniWebView >>> performEvaluate $javascript")
        return "performEvaluate $javascript"
    }

    override fun onDestroy() {
        println("FakeJniWebView >>> onDestroy")
        postBackgroundThread { Looper.main.finish() }
    }

    override fun ensureRunOnBackgroundThread() {

    }

    override fun postMainThread(task: Runnable): Future<*> {
        return super.postMainThread({
            println("postMainThread ${Thread.currentThread().name}")
            task.run()
        })
    }

    override fun getMainThreadFactory(): ThreadFactory {
        return ThreadFactory { task ->
            Thread {
                Looper.main.post(task)
            }
        }
    }

}