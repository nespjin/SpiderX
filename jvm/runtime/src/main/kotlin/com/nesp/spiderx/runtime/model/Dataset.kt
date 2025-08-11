package com.nesp.spiderx.runtime.data

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class Dataset(
    val id: String,
    val url: String,
    val urlCompact: String?,
    val urlMedium: String?,
    val urlExpanded: String?,
    val js: String?,
    val jsCompact: String?,
    val jsMedium: String?,
    val jsExpanded: String?,
    val dsl: Map<String, Any>,
    val dslCompact: Map<String, Any>,
    val dslMedium: Map<String, Any>,
    val dslExpanded: Map<String, Any>,
)