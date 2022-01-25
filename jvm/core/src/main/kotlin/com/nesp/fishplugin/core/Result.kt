package com.nesp.fishplugin.core

open class Result<T> @JvmOverloads constructor(
    var code: Int = CODE_FAILED,
    var message: String = "",
    var data: T? = null,
) {
    companion object {
        const val CODE_FAILED = -1
        const val CODE_SUCCESS = 0

        @JvmStatic
        fun <T> fail(message: String): Result<T> {
            return Result(CODE_FAILED, message)
        }

        @JvmStatic
        fun <T> success(data: T): Result<T> {
            return Result(CODE_SUCCESS, data = data)
        }
    }
}