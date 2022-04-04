package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.data.Page2

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 11:36
 * Description:
 **/
abstract class AbsRuntime : IRuntime {

    protected val tasks = mutableListOf<IRuntimeTask>()
    private val processes = mutableListOf<Process>()

    override fun exec(page: Page2, vararg parameters: Any?): Process {
        return Process(this)
    }

    override fun destroyAllProcess() {
        processes.forEach { it.destroy() }
    }

    override fun getAllProcesses(): MutableList<Process> {
        return processes
    }

    override fun getCurrentProcess(): Process? {
        return processes.firstOrNull()
    }

    protected open fun runTask(task: IRuntimeTask) {
        tasks.add(task)
    }

    protected fun interruptCurrentTask() {
        getCurrentTask()?.interrupt()
        tasks.remove(getCurrentTask())
    }

    protected fun getCurrentTask(): IRuntimeTask? {
        return tasks.firstOrNull()
    }

    protected fun interruptAllTask() {
        tasks.forEach { it.interrupt() }
        tasks.clear()
    }

    companion object {

        private const val TAG = "AbsRuntime"

    }
}