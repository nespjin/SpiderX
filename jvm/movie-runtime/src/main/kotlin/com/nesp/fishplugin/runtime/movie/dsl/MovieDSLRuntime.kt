package com.nesp.fishplugin.runtime.movie.dsl

import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.runtime.Process
import com.nesp.fishplugin.runtime.dsl.DSLRuntime
import com.nesp.fishplugin.runtime.movie.data.HomePage
import kotlin.concurrent.thread

class MovieDSLRuntime : DSLRuntime() {

    override fun exec(page: Page): Process {
        val process = Process()
        thread {
            if (page.id == PAGE_ID_HOME) {
                val homePage = HomePage()
                process.execResult.data = homePage
                process.execResult.exitValue = 0
            }
        }
        return process
    }

    companion object {
        const val PAGE_ID_HOME = "home"
        const val PAGE_ID_CATEGORY = "category"
        const val PAGE_ID_SEARCH = "search"
        const val PAGE_ID_DETAIL = "detail"
    }
}