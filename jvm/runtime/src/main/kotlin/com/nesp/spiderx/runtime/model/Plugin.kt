package com.nesp.spiderx.runtime.data

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class Plugin(
    val id: String,
    val name: String,
    val author: String?,
    val version: String,
    val runtimeVersion: String,
    val description: String?,
    val tags: List<String>,
    val supportedScreenTypes: List<ScreenType>
)