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

package com.nesp.fishplugin.runtime.movie.dsl

import com.nesp.fishplugin.core.data.DSL

class DetailPageDsl(properties: MutableMap<String, String>) : DSL(properties) {

    override fun isAvailable(): Boolean {
        val property = getProperty(PROPERTY_NAME_EPISODE_LIST)
        return property != null && !isPropertyEmpty(property)
    }

    companion object {
        private const val PROPERTY_NAME_PREFIX_EPISODE = "episode"
        private const val PROPERTY_NAME_PREFIX_EPISODE_GROUP = "episodeGroup"

        const val PROPERTY_NAME_EPISODE_LIST =
            PROPERTY_NAME_PREFIX_EPISODE + PROPERTY_CONTENT_LIST
        const val PROPERTY_NAME_EPISODE_GROUP_LIST =
            PROPERTY_NAME_PREFIX_EPISODE_GROUP + PROPERTY_CONTENT_LIST
        const val PROPERTY_NAME_EPISODE_TITLE =
            PROPERTY_NAME_PREFIX_EPISODE + PROPERTY_CONTENT_TITLE
        const val PROPERTY_NAME_EPISODE_URL =
            PROPERTY_NAME_PREFIX_EPISODE + PROPERTY_CONTENT_URL
        const val PROPERTY_NAME_EPISODE_PLAY_URL =
            PROPERTY_NAME_PREFIX_EPISODE + PROPERTY_CONTENT_PLAY_URL
        const val PROPERTY_NAME_EPISODE_LIST_REVERSED =
            PROPERTY_NAME_PREFIX_EPISODE + PROPERTY_CONTENT_LIST + "Reversed"

        const val PROPERTY_NAME_MOVIE_NAME = "name"
        const val PROPERTY_NAME_MOVIE_STATUS = "status"
        const val PROPERTY_NAME_MOVIE_SCORE = "score"
        const val PROPERTY_NAME_MOVIE_STARS = "stars"
        const val PROPERTY_NAME_MOVIE_DIRECTOR = "director"
        const val PROPERTY_NAME_MOVIE_AREA = "area"
        const val PROPERTY_NAME_MOVIE_INTRODUCTION = "introduction"
        const val PROPERTY_NAME_MOVIE_RELEASE_TIME = "releaseTime"

        const val PROPERTY_NAME_MOVIE_DETAIL = "detail"
        const val PROPERTY_NAME_MOVIE_SUBTITLE = "subtitle"
        const val PROPERTY_NAME_MOVIE_TYPE = "type"
        const val PROPERTY_NAME_MOVIE_CATEGORY = "category"

    }

}