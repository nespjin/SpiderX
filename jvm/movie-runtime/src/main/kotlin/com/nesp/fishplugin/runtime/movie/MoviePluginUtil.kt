package com.nesp.fishplugin.runtime.movie

import com.nesp.fishplugin.core.data.Page2
import com.nesp.fishplugin.core.data.Plugin2

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/21 10:43
 * Description:
 **/
object MoviePluginUtil {

    fun findHomePage(plugin: Plugin2): Page2? {
        return plugin.findPageById(MoviePage.HOME.id)
    }

    fun findCategoryPage(plugin: Plugin2): Page2? {
        return plugin.findPage { it.id.startsWith(MOVIE_PAGE_ID_CATEGORY) }
    }

    fun findSearchPage(plugin: Plugin2): Page2? {
        return plugin.findPageById(MoviePage.SEARCH.id)
    }

    fun findDetailPage(plugin: Plugin2): Page2? {
        return plugin.findPageById(MoviePage.DETAIL.id)
    }

}