package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * 电影
 */
open class Movie(

    /**
     * 电影名字
     */
    var name: String = "",

    /**
     * 标题, 在详情页的电影名字下面显示
     */
    var title: String = "",

    /**
     * 状态
     */
    var status: String = "",

    /**
     * 评分
     */
    var score: String = "",

    /**
     * 详情, 点击简介显示电影详情
     */
    var detail: String = "",

    /**
     * 电影源
     */
    var sourceName: String = "",

    /**
     * 电影详情页地址
     */
    var detailUrl: String = "",

    /**
     * 电影封面地址
     */
    var coverImageUrl: String = "",

    /**
     * 播放线路
     */
    var playLines: MutableList<PlayLine> = mutableListOf()

) : Serializable