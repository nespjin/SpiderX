package com.nesp.fishplugin.runtime.js

import com.nesp.fishplugin.runtime.CancellationSignal
import com.nesp.fishplugin.runtime.IRuntimeTask

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 11:44
 * Description:
 **/
interface IJsRuntimeTask<JsEngine> : IRuntimeTask {

    fun run()

    fun run(jsEngine: JsEngine)

    fun execJs(js: String): Any?

    fun isRunning(): Boolean

    fun awaitFinish()

    fun awaitFinish(cancellationSignal: CancellationSignal)

    fun pauseTimers()

    fun resumeTimers()

    fun pause()

    fun resume()

    fun destroy()

    fun prepareJsRuntime() {
        TODO("Not yet implemented")
    }

    fun execJsRuntimeLoadPage(): Any? {
        return execJs("$JS_RUNTIME_FUNCTION_NAME_LOAD_PAGE();")
    }

    fun execJsRuntimeInitialize() {
        execJs("$JS_RUNTIME_FUNCTION_NAME_INITIALIZE();")
    }

    fun execCurrentJs()

    companion object {
        const val JS_RUNTIME_FUNCTION_NAME_LOAD_PAGE = "JsRuntime_LoadPage"
        const val JS_RUNTIME_FUNCTION_NAME_INITIALIZE = "JsRuntime_Initialize"
    }
}