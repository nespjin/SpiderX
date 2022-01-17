package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * 搜索页面
 */
class SearchPage : Serializable {

    /**
     * 电影集合
     */
    var movies: MutableList<Movie> = mutableListOf()

    /**
     * 下页地址，如果不存在下一页则为空字符串
     */
    var nextPageUrl: String = ""
}