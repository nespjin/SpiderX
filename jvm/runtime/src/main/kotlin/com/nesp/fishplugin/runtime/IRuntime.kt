package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.data.Page
import java.util.concurrent.TimeUnit

interface IRuntime {

    fun exec(page: Page): Process

    fun runTask(task: Runnable)

    fun interruptCurrentTask()

    fun shutdown()

    fun shutdownNow()

    fun awaitTermination(timeout: Long, unit: TimeUnit)

    fun isTerminated(): Boolean

}