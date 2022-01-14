package com.nesp.fishplugin.core

class Environment private constructor() {

    fun getDeviceType(): Int {
        return DEVICE_TYPE_MOBILE_PHONE
    }

    companion object {

        ///////////////////////////////////////////////////////////////////////////
        // Device Type
        ///////////////////////////////////////////////////////////////////////////
        const val DEVICE_TYPE_MOBILE_PHONE = 0
        const val DEVICE_TYPE_TABLE = 1
        const val DEVICE_TYPE_DESKTOP = 2

        @JvmStatic
        val default: Environment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            return@lazy Environment()
        }
    }
}