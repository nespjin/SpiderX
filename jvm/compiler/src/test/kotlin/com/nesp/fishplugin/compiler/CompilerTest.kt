package com.nesp.fishplugin.compiler

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File

internal class CompilerTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun compileFromDisk() {
        val compileFromDisk = Compiler.compileFromDisk("./plugin.json")
        println("compile: message = " + compileFromDisk.message)
    }
}