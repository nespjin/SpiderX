package com.nesp.fishplugin.runtime.android.js

import android.content.Context
import android.os.CancellationSignal
import android.os.SystemClock
import com.nesp.fishplugin.runtime.IRuntimeTask
import com.nesp.fishplugin.runtime.js.JsRuntime
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 1:33
 * Description:
 **/
open class AndroidJsRuntime(context: Context) : JsRuntime() {

    override fun runTask(task: IRuntimeTask) {
        if (task is AndroidJsRuntimeTask) {
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
            if (it is AndroidJsRuntimeTask) {
                it.awaitFinish()
                it.destroy()
            }
        }
    }

    @Synchronized
    override fun shutdownNow() {
        tasks.forEach {
            if (it is AndroidJsRuntimeTask) {
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

                SystemClock.sleep(50)
            }
        }
        tasks.forEach {
            if (it is AndroidJsRuntimeTask) {
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