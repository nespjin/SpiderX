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

package com.nesp.fishplugin.core.data

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PluginTest {

    private lateinit var plugin: Plugin

    @BeforeEach
    fun setUp() {
        print("setUp")
        plugin = Plugin(deviceFlags = 0b0000_0101)
    }

    @AfterEach
    fun tearDown() {

    }

    @Test
    fun isSupportMobilePhone() {
        assertEquals(true, plugin.isSupportMobilePhone())
    }

    @Test
    fun isSupportTable() {
        assertEquals(false, plugin.isSupportTable())
    }

    @Test
    fun isSupportDesktop() {
        assertEquals(true, plugin.isSupportDesktop())
    }
}