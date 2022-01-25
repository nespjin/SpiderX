package com.nesp.fishplugin.runtime.js

import com.nesp.fishplugin.core.utils.LruCache
import com.nesp.fishplugin.runtime.AbsRuntime
import com.nesp.fishplugin.runtime.CancellationSignal
import com.nesp.fishplugin.runtime.IRuntimeTask
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import java.lang.Runtime as JavaRuntime

abstract class JsRuntime : AbsRuntime() {

    ///////////////////////////////////////////////////////////////////////////
    // Html
    ///////////////////////////////////////////////////////////////////////////

    protected val htmlDocumentStringCache: LruCache<String/*Url*/, String/*Html*/>

    init {
        val maxSize = JavaRuntime.getRuntime().maxMemory() / 8
        htmlDocumentStringCache = LruCache(maxSize.toInt())
    }

    override fun runTask(task: IRuntimeTask) {
        if (task is IJsRuntimeTask<*>) {
            super.runTask(task)
            task.run()
        }
    }

    /**
     * may blocking thread
     */
    @Synchronized
    override fun shutdown() {
        tasks.forEach {
            if (it is IJsRuntimeTask<*>) {
                it.awaitFinish()
                it.destroy()
            }
        }
    }

    @Synchronized
    override fun shutdownNow() {
        tasks.forEach {
            if (it is IJsRuntimeTask<*>) {
                it.destroy()
            }
        }
    }

    private var _isTerminated = false

    @Synchronized
    override fun awaitTermination(timeout: Long, unit: TimeUnit) {
        val beginTimeMillis = System.currentTimeMillis()
        val cancellationSignal = CancellationSignal()
        thread(isDaemon = true) {
            while (true) {
                if (System.currentTimeMillis() - beginTimeMillis >=
                    TimeUnit.MILLISECONDS.convert(timeout, unit)
                ) {
                    cancellationSignal.cancel()
                    break
                }

                if (cancellationSignal.isCanceled) {
                    break
                }

                Thread.sleep(50)
            }
        }
        tasks.forEach {
            if (it is IJsRuntimeTask<*>) {
                it.awaitFinish(cancellationSignal)
                it.destroy()
            }
        }
        _isTerminated = true
        cancellationSignal.cancel()
    }

    @Synchronized
    override fun isTerminated(): Boolean {
        return _isTerminated
    }

}