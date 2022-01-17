package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * 剧集
 */
data class Episode(
    /**
     * 剧集标题
     */
    var title: String = "",

    /**
     * 页面地址
     */
    var pageUrl: String = "",

    /**
     * 视频播放地址
     */
    var playUrl: String = "",
) : Serializable