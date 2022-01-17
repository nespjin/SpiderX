package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * Movie classification
 */
data class MovieCategory(
    val title: String = "",
    val pageUrl: String = ""
) : Serializable