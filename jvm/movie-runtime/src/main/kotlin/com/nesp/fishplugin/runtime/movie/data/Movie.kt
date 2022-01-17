package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * Movie
 */
open class Movie(

    /**
     * movie name
     */
    var name: String = "",

    /**
     * state
     */
    var status: String = "",

    /**
     * score
     */
    var score: String = "",

    /**
     * 明星
     */
    var stars: String = "",

    /**
     * 导演
     */
    var director: String = "",

    /**
     * 地区
     */
    var area: String = "",

    /**
     * 简介
     */
    var introduction: String = "",

    /**
     * 上映时间
     */
    var releaseTime: String = "",

    /**
     * movie source
     */
    var sourceName: String = "",

    /**
     * Movie details page url
     */
    var detailUrl: String = "",

    /**
     * movie cover image url
     */
    var coverImageUrl: String = "",

    /**
     * Play lines
     */
    var playLines: MutableList<PlayLine> = mutableListOf()

) : Serializable {

    /**
     * Returns Title, displayed under the movie name on the detail page
     */
    fun getTitle(): String {
        return ""
    }

    fun getDetail(): String {
        return ""
    }

}