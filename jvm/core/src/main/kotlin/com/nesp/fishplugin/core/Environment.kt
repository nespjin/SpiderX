package com.nesp.fishplugin.core

import com.nesp.fishplugin.core.data.Plugin2

class Environment private constructor() {

    private val build: Build

    init {
        // Load build from build.prop
        build = Build()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Device Type
    ///////////////////////////////////////////////////////////////////////////

    fun getDeviceType(): Int {
        return build.deviceType
    }

    fun isMobilePhone(): Boolean {
        return getDeviceType() == DEVICE_TYPE_MOBILE_PHONE
    }

    fun isTable(): Boolean {
        return getDeviceType() == DEVICE_TYPE_TABLE
    }

    fun isDesktop(): Boolean {
        return getDeviceType() == DEVICE_TYPE_DESKTOP
    }

    fun isSupport(plugin: Plugin2): Boolean {
        if (isMobilePhone() && plugin.isSupportMobilePhone()) {
            return true
        } else if (isTable() && plugin.isSupportTable()) {
            return true
        } else if (isDesktop() && plugin.isSupportDesktop()) {
            return true
        }
        return false
    }

    fun getBuild(): Build {
        return build
    }

    /**
     * File name:
     * build.properties
     */
    class Build {
        ///////////////////////////////////////////////////////////////////////////
        // Device Type
        ///////////////////////////////////////////////////////////////////////////
        val deviceType = DEVICE_TYPE_MOBILE_PHONE // device.type

        ///////////////////////////////////////////////////////////////////////////
        // Runtime
        ///////////////////////////////////////////////////////////////////////////
        val runtimeApiLevel = RUNTIME_API_LEVEL_A // runtime.api.level
        val runtimeVersionCode = 1 // runtime.version.code
        val runtimeVersionName = "1.0.0" // runtime.version.name
        val runtimeBuild = "2022.01.16-NESP" // runtime.build

        companion object {
            const val RUNTIME_API_LEVEL_A = 1
        }
    }

    companion object {

        ///////////////////////////////////////////////////////////////////////////
        // Device Type
        ///////////////////////////////////////////////////////////////////////////
        const val DEVICE_TYPE_MOBILE_PHONE = 0
        const val DEVICE_TYPE_TABLE = 1
        const val DEVICE_TYPE_DESKTOP = 2

        @JvmStatic
        fun allDeviceTypes(): Array<Int> {
            return arrayOf(DEVICE_TYPE_MOBILE_PHONE, DEVICE_TYPE_TABLE, DEVICE_TYPE_DESKTOP)
        }

        @JvmStatic
        val shared: Environment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            return@lazy Environment()
        }
    }
}