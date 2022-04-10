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

package com.nesp.fishplugin.compiler

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GrammarTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun applyVariableValue() {
        val variableNameAndValue = hashMapOf<String, String>()
        variableNameAndValue["name"] = "Jack"
        variableNameAndValue["age"] = "1"
        val s = Grammar.applyVariableValue(
            "it is {{name}}, name = {{name}}, age = {{age}}",
            variableNameAndValue
        )
        assertEquals("it is Jack, name = Jack, age = 1", s)
    }

    @Test
    fun lookupVariables() {
        val lookupVariables = Grammar.lookupVariables("it is {{name}},age = {{age}}")
        assertEquals(2, lookupVariables.size)
        assertEquals("name", lookupVariables[0].name)
        assertEquals("age", lookupVariables[1].name)
    }
}