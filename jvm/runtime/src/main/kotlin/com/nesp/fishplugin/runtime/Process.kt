package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.Result

class Process(val owner: IRuntime) {

    var isDestroy = false
        private set

    val execResult = ExecResult(EXIT_VALUE_NULL)

    var onDestroyListener: OnDestroyListener? = null

    fun destroy() {
        isDestroy = true
        exit(EXIT_VALUE_BY_USER)
        onDestroyListener?.onDestroy()
    }

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

    fun waitFor(): ExecResult {
        while (execResult.exitValue == EXIT_VALUE_NULL) {
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
        @Volatile
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