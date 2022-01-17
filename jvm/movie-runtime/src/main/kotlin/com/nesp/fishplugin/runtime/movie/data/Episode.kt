package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * episode
 */
data class Episode(
    /**
     * Episode title
     */
    var title: String = "",

    /**
     * page url
     */
    var pageUrl: String = "",

    /**
     * Video playback URL
     */
    var playUrl: String = "",
) : Serializable