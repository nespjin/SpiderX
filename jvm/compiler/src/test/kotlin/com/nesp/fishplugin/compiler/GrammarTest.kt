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