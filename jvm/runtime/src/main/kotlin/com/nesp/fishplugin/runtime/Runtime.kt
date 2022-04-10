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

package com.nesp.fishplugin.runtime

import com.nesp.fishplugin.core.data.Page2

class Runtime {

    var runtimeFactory: IRuntimeFactory? = null

    fun execJs(page: Page2): Process {
        return exec(page, EXEC_TYPE_JS)
    }

    fun execDsl(page: Page2): Process {
        return exec(page, EXEC_TYPE_DSL)
    }

    fun exec(page: Page2, execType: Int): Process {
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