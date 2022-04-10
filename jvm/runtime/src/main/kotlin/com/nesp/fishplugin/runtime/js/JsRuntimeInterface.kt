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

package com.nesp.fishplugin.runtime.js

import com.nesp.fishplugin.core.Environment

abstract class JsRuntimeInterface : IJsRuntimeInterface {

    override fun getApiLevel(): Int {
        return Environment.shared.getBuild().runtimeApiLevel
    }

    override fun getVersionCode(): Int {
        return Environment.shared.getBuild().runtimeVersionCode
    }

    override fun getVersionName(): String {
        return Environment.shared.getBuild().runtimeVersionName
    }

    override fun getBuild(): String {
        return Environment.shared.getBuild().runtimeBuild
    }

    override fun getDeviceType(): Int {
        return Environment.shared.getDeviceType()
    }

    override fun isMobilePhone(): Boolean {
        return Environment.shared.isMobilePhone()
    }

    override fun isTable(): Boolean {
        return Environment.shared.isTable()
    }

    override fun isDesktop(): Boolean {
        return Environment.shared.isDesktop()
    }

    override fun sendPage2Platform(page: String) {
    }

    override fun sendError2Platform(errorMsg: String) {
    }

    override fun printHtml(html: String) {

    }
}