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

package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * Movie
 */
open class Movie(

    /**
     * movie name
     */
    var name: String = "",

    /**
     * state
     */
    var status: String = "",

    /**
     * score
     */
    var score: String = "",

    /**
     * 明星
     */
    var stars: String = "",

    /**
     * 导演
     */
    var director: String = "",

    /**
     * 地区
     */
    var area: String = "",

    /**
     * 简介
     */
    var introduction: String = "",

    /**
     * 上映时间
     */
    var releaseTime: String = "",

    /**
     * movie source
     */
    var sourceName: String = "",

    /**
     * Movie details page url
     */
    var detailUrl: String = "",

    /**
     * movie cover image url
     */
    var coverImageUrl: String = "",

    var detail: String = "",

    var subtitle: String = "",

    var type: String = "",

    var category: String = "",

    /**
     * Play lines
     */
    var playLines: MutableList<PlayLine> = mutableListOf()

) : Serializable