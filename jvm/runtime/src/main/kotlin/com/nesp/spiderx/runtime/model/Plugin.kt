package com.nesp.spiderx.runtime.model

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/

data class Plugin @JvmOverloads constructor(
    val id: String = "",
    val name: String = "",
    val author: String? = null,
    val version: String = "",
    val runtimeVersion: String = "",
    val description: String? = null,
    val tags: List<String> = arrayListOf(),
    val supportedScreenTypes: List<ScreenType> = arrayListOf(),
    val datasets: List<Dataset> = arrayListOf(),
)