package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.data.Page
import java.util.concurrent.TimeUnit

interface IRuntime {

    fun exec(page: Page, vararg parameters: Any?): Process

    fun getCurrentProcess(): Process?

    fun destroyAllProcess()

    fun getAllProcesses(): MutableList<Process>

    fun shutdown()

    fun shutdownNow()

    fun awaitTermination(timeout: Long, unit: TimeUnit)

    fun isTerminated(): Boolean

}