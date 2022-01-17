package com.nesp.fishplugin.runtime.movie.data

import java.io.Serializable


/**
 * home page
 */
class HomePage : Serializable {
    /**
     * carousel
     */
    var slideMovies: MutableList<Movie> = mutableListOf()

    /**
     * latest play
     */
    var newPlay: MutableList<Movie> = mutableListOf()

    /**
     * New Movies
     */
    var newMovie: MutableList<Movie> = mutableListOf()

    /**
     * latest TV series
     */
    var newSoap: MutableList<Movie> = mutableListOf()

    /**
     * latest variety show
     */
    var newVariety: MutableList<Movie> = mutableListOf()

    /**
     * latest anime
     */
    var newAnim: MutableList<Movie> = mutableListOf()
}