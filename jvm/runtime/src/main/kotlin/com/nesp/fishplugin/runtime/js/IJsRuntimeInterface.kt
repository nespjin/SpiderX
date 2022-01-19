package com.nesp.fishplugin.runtime.js

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 1:21
 * Description:
 **/
interface IJsRuntimeInterface : IEventJsRuntimeInterface {

    fun getApiLevel(): Int

    fun getVersionCode(): Int

    fun getVersionName(): String

    fun getBuild(): String

    fun getDeviceType(): Int

    fun isMobilePhone(): Boolean

    fun isTable(): Boolean

    fun isDesktop(): Boolean


}