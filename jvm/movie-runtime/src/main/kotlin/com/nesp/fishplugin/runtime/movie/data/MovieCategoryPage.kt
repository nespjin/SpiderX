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