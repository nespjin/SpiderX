package com.nesp.fishplugin.runtime

interface IRuntimeFactory {
    fun buildRuntime(execType: Int): IRuntime
}