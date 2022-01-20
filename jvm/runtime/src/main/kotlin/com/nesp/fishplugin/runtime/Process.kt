package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.Result

class Process(val owner: IRuntime) {

    var isDestroy = false
        private set

    val execResult = ExecResult(-1)

    var onDestroyListener: OnDestroyListener? = null

    @Synchronized
    fun destroy() {
        isDestroy = true
        exit(EXIT_VALUE_BY_USER)
        onDestroyListener?.onDestroy()
    }

    @Synchronized
    fun exit(exitValue: Int) {
        execResult.exitValue = exitValue
        owner.getAllProcesses().remove(this)
    }

    fun exitNormally() {
        exit(EXIT_VALUE_NORMAL)
    }

    fun exitWithError() {
        exit(EXIT_VALUE_ERROR)
    }

    @Synchronized
    fun waitFor(): ExecResult {
        while (execResult.exitValue != EXIT_VALUE_NULL) {
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
        return execResult
    }

    class ExecResult(
        var exitValue: Int,
        message: String = "",
        data: Any? = null
    ) : Result<Any?>(exitValue, message, data)

    interface OnDestroyListener {
        fun onDestroy();
    }

    companion object {
        const val EXIT_VALUE_NULL = -1
        const val EXIT_VALUE_NORMAL = 0
        const val EXIT_VALUE_BY_USER = 1
        const val EXIT_VALUE_ERROR = 2
    }

}