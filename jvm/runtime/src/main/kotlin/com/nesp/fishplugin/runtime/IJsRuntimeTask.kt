package com.nesp.fishplugin.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 11:44
 * Description:
 **/
interface IJsRuntimeTask : IRuntimeTask {

    fun execJs(js: String): Any?

    fun prepareJsRuntime() {
        TODO("Not yet implemented")
    }

    fun execJsRuntimeLoadPage(): Any? {
        return execJs("$JS_RUNTIME_FUNCTION_NAME_LOAD_PAGE();")
    }

    fun execJsRuntimeInitialize() {
        execJs("$JS_RUNTIME_FUNCTION_NAME_INITIALIZE();")
    }

    companion object {
        const val JS_RUNTIME_FUNCTION_NAME_LOAD_PAGE = "JsRuntime_LoadPage"
        const val JS_RUNTIME_FUNCTION_NAME_INITIALIZE = "JsRuntime_Initialize"
    }
}