package com.nesp.fishplugin.runtime.movie.dsl

import com.nesp.fishplugin.core.data.DSL

class SearchPageDsl(properties: MutableMap<String, String>) : DSL(properties) {

    override fun isAvailable(): Boolean {
        val property = getProperty(PROPERTY_NAME_MOVIE_LIST)
        return property != null && !isPropertyEmpty(property)
    }

    companion object {
        private const val PROPERTY_NAME_PREFIX_MOVIE = "movie"
        private const val PROPERTY_NAME_PREFIX_NEXT_PAGE = "nextPage"

        const val PROPERTY_NAME_MOVIE_LIST =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_LIST
        const val PROPERTY_NAME_MOVIE_COVER_IMAGE =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_COVER_IMAGE
        const val PROPERTY_NAME_MOVIE_CATEGORY =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_CATEGORY
        const val PROPERTY_NAME_MOVIE_STARS =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_STARS
        const val PROPERTY_NAME_MOVIE_DIRECTOR =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_DIRECTOR
        const val PROPERTY_NAME_MOVIE_RELEASE_TIME =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_RELEASE_TIME
        const val PROPERTY_NAME_MOVIE_TYPE =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_TYPE
        const val PROPERTY_NAME_MOVIE_NAME =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_NAME
        const val PROPERTY_NAME_MOVIE_SCORE =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_SCORE
        const val PROPERTY_NAME_MOVIE_DETAIL_URL =
            PROPERTY_NAME_PREFIX_MOVIE + PROPERTY_CONTENT_DETAIL_URL

        const val PROPERTY_NAME_MOVIE_NEXT_PAGE_URL =
            PROPERTY_NAME_PREFIX_NEXT_PAGE + PROPERTY_CONTENT_URL


    }

}