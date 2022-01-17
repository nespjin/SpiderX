package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * 播放线路
 */
data class PlayLine(
    /**
     * 标题
     */
    var title: String = "",

    /**
     * 剧集列表
     */
    var episodes: MutableList<Episode> = mutableListOf()
) : Serializable