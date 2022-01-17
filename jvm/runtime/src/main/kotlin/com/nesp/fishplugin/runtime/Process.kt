package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.Result

class Process<T> {

    var isDestroy = false
        private set

    val execResult = ExecResult<T>(-1)

    fun destroy() {
        isDestroy = true
    }

    fun waitFor(): ExecResult<T> {
        while (execResult.exitValue != -1) {
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
        return execResult
    }

    class ExecResult<T>(
        var exitValue: Int,
        message: String = "",
        data: T? = null
    ) : Result<T>(exitValue, message, data)

}