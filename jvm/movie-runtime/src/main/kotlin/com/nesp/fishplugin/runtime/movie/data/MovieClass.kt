package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * 电影分类
 */
data class MovieClass(
    val title: String = "",
    val pageUrl: String = ""
) : Serializable