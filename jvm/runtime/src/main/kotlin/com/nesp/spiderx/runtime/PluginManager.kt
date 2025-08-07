/*
 * Copyright (c) 2025.  NESP Technology.
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

package com.nesp.spiderx.runtime

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class PluginManager {

    external fun nativeInit(databasePath: String)

    external fun nativeInstallPlugin(type: Int, source: String)

    companion object {
        private const val TAG = "PluginManager"

        private const val PLUGIN_SOURCE_TYPE_JSON = 0
        private const val PLUGIN_SOURCE_TYPE_JSON_FILE = 1

        private const val DYNAMIC_LIB_NAME = "spiderx_runtime"

        init {
            System.loadLibrary(DYNAMIC_LIB_NAME)
        }
    }
}