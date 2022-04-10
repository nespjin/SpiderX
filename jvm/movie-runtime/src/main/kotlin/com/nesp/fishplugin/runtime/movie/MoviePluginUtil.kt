/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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