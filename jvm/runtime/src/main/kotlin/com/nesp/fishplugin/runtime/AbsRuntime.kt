/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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