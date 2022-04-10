/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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