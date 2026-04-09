package com.nesp.spiderx.runtime.model

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
data class Dataset @JvmOverloads constructor(
    val id: String = "",
    val url: String = "",
    val urlCompact: String? = null,
    val urlMedium: String? = null,
    val urlExpanded: String? = null,
    val js: String? = null,
    val jsCompact: String? = null,
    val jsMedium: String? = null,
    val jsExpanded: String? = null,
    val dsl: Map<String, Any> = mapOf(),
    val dslCompact: Map<String, Any> = mapOf(),
    val dslMedium: Map<String, Any> = mapOf(),
    val dslExpanded: Map<String, Any> = mapOf(),
    val nextDatasetId: String? = null,
)