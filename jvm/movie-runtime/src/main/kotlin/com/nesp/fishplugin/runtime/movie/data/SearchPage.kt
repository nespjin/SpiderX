package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable

/**
 * search page
 */
class SearchPage : Serializable {

    /**
     * movie collection
     */
    var movies: MutableList<Movie> = mutableListOf()

    /**
     * The address of the next page, or an empty string if there is no next page
     */
    var nextPageUrl: String = ""
}