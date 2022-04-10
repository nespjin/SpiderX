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
 * Movie Category Page
 */
class MovieCategoryPage : Serializable {

    /**
     * Movie Classification Information
     */
    var movieCategoryGroups: MutableList<MovieCategoryGroup> = mutableListOf()

    /**
     * movie list
     */
    var movies: MutableList<Movie> = mutableListOf()

    /**
     * The url of the next page, or an empty string if the next page does not exist
     */
    var nextPageUrl: String = ""
}