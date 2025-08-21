package com.nesp.spiderx.runtime

import org.apache.logging.log4j.LogManager
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
abstract class JniWebView {
    private var mPtr = -1L

    private val backgroundExecutor = Executors.newSingleThreadExecutor()

    fun init() {
        LOGGER.trace("init")
        ensureRunOnBackgroundThread()
        postMainThread(::onInit).get()
        LOGGER.trace("init finished")
    }

    abstract fun onInit()

    fun loadUrl(url: String) {
        LOGGER.trace("loadUrl")
        ensureRunOnBackgroundThread()
        postMainThread({ performLoadUrl(url) }).get()
        LOGGER.trace("loadUrl finished")
    }

    abstract fun performLoadUrl(url: String)

    fun loadData(data: String) {
        ensureRunOnBackgroundThread()
        postMainThread({ performLoadData(data) }).get()
    }

    abstract fun performLoadData(data: String)

    fun reload() {
        ensureRunOnBackgroundThread()
        postMainThread(::performReload).get()
    }

    abstract fun performReload()

    fun evaluate(javascript: String): String? {
        LOGGER.trace("evaluate: $javascript")
        ensureRunOnBackgroundThread()
        return postMainThread(Callable { return@Callable performEvaluate(javascript) }).get()
    }

    abstract fun performEvaluate(javascript: String): String?

    fun destroy() {
        LOGGER.trace("destroy")
        ensureRunOnBackgroundThread()
        postMainThread(::onDestroy).get()
        finishBackgroundThread()
        LOGGER.trace("destroy finished")
    }

    abstract fun onDestroy()

    fun notifyOnPageStarted(url: String) {
        nativeNotifyOnPageStarted(url)
    }

    private external fun nativeNotifyOnPageStarted(url: String)

    fun notifyOnPageCancelled(url: String) {
        nativeNotifyOnPageCancelled(url)
    }

    private external fun nativeNotifyOnPageCancelled(url: String)

    fun notifyOnPageFinished(url: String, document: String) {
        nativeNotifyOnPageFinished(url, document)
    }

    private external fun nativeNotifyOnPageFinished(url: String, document: String)

    fun notifyOnPageError(url: String, error: String) {
        nativeNotifyOnPageError(url, error)
    }

    private external fun nativeNotifyOnPageError(url: String, error: String)

    fun notifyOnLoadProgress(progress: Int) {
        nativeNotifyOnLoadProgress(progress)
    }

    private external fun nativeNotifyOnLoadProgress(progress: Int)

    fun notifyOnShouldOverrideUrlLoading(url: String): Boolean {
        return nativeNotifyOnShouldOverrideUrlLoading(url)
    }

    private external fun nativeNotifyOnShouldOverrideUrlLoading(url: String): Boolean

    fun notifyOnShouldInterceptRequest(url: String): String? {
        return nativeNotifyOnShouldInterceptRequest(url)
    }

    private external fun nativeNotifyOnShouldInterceptRequest(url: String): String?

    fun notifyOnReceivedData(url: String, data: String) {
        nativeNotifyOnReceivedData(url, data)
    }

    private external fun nativeNotifyOnReceivedData(url: String, data: String)

    fun notifyOnReceivedError(url: String, error: String) {
        nativeNotifyOnReceivedError(url, error)
    }

    private external fun nativeNotifyOnReceivedError(url: String, error: String)


    private fun finishBackgroundThread() {
        backgroundExecutor.shutdown()
    }

    open fun waitBackgroundThread(task: Runnable) {
        val countDownLatch = CountDownLatch(1)
        backgroundExecutor.execute({
            task.run()
            countDownLatch.countDown()
        })
        countDownLatch.await()
    }

    open fun postBackgroundThread(task: Runnable): Future<*> {
        return backgroundExecutor.submit(task)
    }

    open fun <T> postBackgroundThread(task: Callable<T>): Future<T> {
        return backgroundExecutor.submit(task)
    }

    open fun ensureRunOnBackgroundThread() {
        if (isMainThread()) {
            throw IllegalStateException("Must be called on the background thread")
        }
    }

    open fun isMainThread(): Boolean = true

    open fun waitMainThread(task: Runnable) {
        val countDownLatch = CountDownLatch(1)
        dispatchMainThread({
            task.run()
            countDownLatch.countDown()
        })
        countDownLatch.await()
    }

    open fun postMainThread(task: Runnable): Future<*> {
        val result = CompletableFuture<Any>()
        dispatchMainThread {
            try {
                task.run()
                result.complete(null)
            } catch (e: Exception) {
                result.completeExceptionally(e)
            }
        }
        return result
    }

    open fun <T> postMainThread(task: Callable<T>): Future<T> {
        val result = CompletableFuture<T>()
        dispatchMainThread {
            try {
                result.complete(task.call())
            } catch (e: Exception) {
                result.completeExceptionally(e)
            }
        }
        return result
    }

    open fun dispatchMainThread(task: Runnable) {
        task.run()
    }

    interface SpiderXRuntimeJavaScriptObject {
        fun sendData(data: String)

        fun sendError(error: String)
    }

    companion object {
        private val LOGGER = LogManager.getLogger(EmptyJniWebView::class.java)
        const val SPIDERX_RUNTIME_JAVASCRIPT_OBJECT_NAME = "spiderxRT"
    }
}