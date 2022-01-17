package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.data.Page

class Runtime {

    var runtimeFactory: IRuntimeFactory? = null

    fun <T> execJs(page: Page): Process<T> {
        return exec(page, EXEC_TYPE_JS)
    }

    fun <T> execDsl(page: Page): Process<T> {
        return exec(page, EXEC_TYPE_DSL)
    }

    fun <T> exec(page: Page, execType: Int): Process<T> {
        if (runtimeFactory == null) {
            throw IllegalArgumentException("The runtimeFactory not set yet!")
        }
        return runtimeFactory!!.buildRuntime(execType).exec(page)
    }

    companion object {

        const val EXEC_TYPE_JS = 0
        const val EXEC_TYPE_DSL = 1

        val shared: Runtime by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Runtime()
        }
    }
}