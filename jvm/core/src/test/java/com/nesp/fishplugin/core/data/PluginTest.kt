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
        plugin = Plugin(deviceFlag = 0b0000_0101)
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