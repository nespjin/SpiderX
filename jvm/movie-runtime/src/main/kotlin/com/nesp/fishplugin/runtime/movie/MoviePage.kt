package com.nesp.fishplugin.runtime.movie

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 13:05
 * Description:
 **/

const val PAGE_ID_CATEGORY = "category_"

enum class MoviePage(
    val id: String
) {
    HOME("home"),
    CATEGORY_MOVIE("${PAGE_ID_CATEGORY}movie"),
    CATEGORY_SOAP("${PAGE_ID_CATEGORY}soap"),
    CATEGORY_VARIETY("${PAGE_ID_CATEGORY}variety"),
    CATEGORY_ANIM("${PAGE_ID_CATEGORY}anim"),
    SEARCH("search"),
    DETAIL("detail"),
    ;
}