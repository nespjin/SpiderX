package com.nesp.fishplugin.core

open class Result<T>(
    var code: Int = CODE_FAILED,
    var message: String = "",
    var data: T? = null,
) {
    companion object {
        const val CODE_FAILED = -1
        const val CODE_SUCCESS = 0
    }
}