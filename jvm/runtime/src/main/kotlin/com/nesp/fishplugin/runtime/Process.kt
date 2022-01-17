package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.Result

class Process {

    var isDestroy = false
        private set

    val execResult = ExecResult(-1)

    fun destroy() {
        isDestroy = true
    }

    fun waitFor(): ExecResult {
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

    class ExecResult(
        var exitValue: Int,
        message: String = "",
        data: Any? = null
    ) : Result<Any?>(exitValue, message, data)

}