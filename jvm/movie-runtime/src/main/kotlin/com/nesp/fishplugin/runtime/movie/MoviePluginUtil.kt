package com.nesp.fishplugin.runtime.movie

import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.core.data.Plugin

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/21 10:43
 * Description:
 **/
object MoviePluginUtil {

    fun findHomePage(plugin: Plugin): Page? {
        return plugin.findPageById(MoviePage.HOME.id)
    }

    fun findCategoryPage(plugin: Plugin): Page? {
        return plugin.findPage { it.id.startsWith(MOVIE_PAGE_ID_CATEGORY) }
    }

    fun findSearchPage(plugin: Plugin): Page? {
        return plugin.findPageById(MoviePage.SEARCH.id)
    }

    fun findDetailPage(plugin: Plugin): Page? {
        return plugin.findPageById(MoviePage.DETAIL.id)
    }

}