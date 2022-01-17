package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.data.Page

interface IRuntime {

    fun <T> exec(page: Page): Process<T>

}