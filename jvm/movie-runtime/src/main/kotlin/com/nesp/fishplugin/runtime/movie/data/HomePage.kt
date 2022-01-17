package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable


/**
 * 主页面
 */
class HomePage : Serializable {
    /**
     * 轮播
     */
    var slideMovies: MutableList<Movie> = mutableListOf()

    /**
     * 最新播放
     */
    var newPlay: MutableList<Movie> = mutableListOf()

    /**
     * 最新电影
     */
    var newMovie: MutableList<Movie> = mutableListOf()

    /**
     * 最新电视剧
     */
    var newSoap: MutableList<Movie> = mutableListOf()

    /**
     * 最新综艺
     */
    var newVariety: MutableList<Movie> = mutableListOf()

    /**
     * 最新动漫
     */
    var newAnim: MutableList<Movie> = mutableListOf()
}