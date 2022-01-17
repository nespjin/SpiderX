package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * Play lines
 */
data class PlayLine(
    /**
     * title
     */
    var title: String = "",

    /**
     * episode list
     */
    var episodes: MutableList<Episode> = mutableListOf()
) : Serializable