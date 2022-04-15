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

import com.nesp.fishplugin.core.data.Page2
import com.nesp.fishplugin.runtime.IRunnableRuntimeTask
import com.nesp.fishplugin.runtime.Process
import com.nesp.fishplugin.runtime.dsl.DslRuntime
import com.nesp.fishplugin.runtime.movie.MoviePage
import com.nesp.fishplugin.runtime.movie.MOVIE_PAGE_ID_CATEGORY
import com.nesp.fishplugin.runtime.movie.data.*
import org.jsoup.nodes.Document

class MovieDslRuntime : DslRuntime() {

    override fun exec(page: Page2, vararg parameters: Any?): Process {
        val process = super.exec(page, parameters)
        runTask(object : IRunnableRuntimeTask {
            override fun run() {
                when {
                    page.id == MoviePage.HOME.id -> {
                        execHomePage(page, process)
                    }
                    page.id.startsWith(MOVIE_PAGE_ID_CATEGORY) -> {
                        execMovieCategoryPage(page, process)
                    }
                    page.id == MoviePage.SEARCH.id -> {
                        execSearchPage(page, process)
                    }
                    page.id == MoviePage.DETAIL.id -> {
                        execDetailPage(page, process)
                    }
                    else -> {
                        process.exitWithError()
                    }
                }
            }
        })
        return process
    }

    private fun execDetailPage(page: Page2, process: Process) {
        if (!isPageAvailable(page)) {
            process.exitWithError()
            interruptCurrentTask()
            return
        }

        val movie = Movie()

        // Create html document or get from cache
        val document = htmlDocumentCache[page.getUrl()]

        @Suppress("UNCHECKED_CAST")
        val dsl = DetailPageDsl(page.getDsl()!! as MutableMap<String, String>)

        val episodeGroupListElements =
            select(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_EPISODE_GROUP_LIST))
        for (episodeGroupListElement in episodeGroupListElements) {
            if (process.isDestroy) {
                interruptCurrentTask()
                return
            }
            val playLine = PlayLine()
            val episodeListElements = select(
                episodeGroupListElement,
                dsl.getProperty(DetailPageDsl.PROPERTY_NAME_EPISODE_LIST)
            )
            for (episodeListElement in episodeListElements) {
                if (process.isDestroy) {
                    interruptCurrentTask()
                    return
                }
                val title = getValueByDsl(
                    episodeListElement,
                    dsl.getProperty(DetailPageDsl.PROPERTY_NAME_EPISODE_TITLE)
                )
                val url = getValueByDsl(
                    episodeListElement,
                    dsl.getProperty(DetailPageDsl.PROPERTY_NAME_EPISODE_URL)
                )
                val playUrl = getValueByDsl(
                    episodeListElement,
                    dsl.getProperty(DetailPageDsl.PROPERTY_NAME_EPISODE_PLAY_URL)
                )
                playLine.episodes.add(
                    Episode(title, url, playUrl)
                )
            }

            val isEpisodeReversed =
                dsl.getProperty(DetailPageDsl.PROPERTY_NAME_EPISODE_LIST_REVERSED) == "true"
            if (isEpisodeReversed) {
                movie.playLines.reverse()
            }
            movie.playLines.add(playLine)
        }

        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_NAME)).also {
            if (it.isNotEmpty()) movie.name = it
        }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_DIRECTOR)).also {
            if (it.isNotEmpty()) movie.director = it
        }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_STARS)).also {
            if (it.isNotEmpty()) movie.stars = it
        }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_STATUS)).also {
            if (it.isNotEmpty()) movie.status = it
        }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_AREA)).also {
            if (it.isNotEmpty()) movie.area = it
        }

        if (process.isDestroy) {
            interruptCurrentTask()
            return
        }

        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_SCORE)).also {
            if (it.isNotEmpty()) movie.score = it
        }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_RELEASE_TIME))
            .also { if (it.isNotEmpty()) movie.releaseTime = it }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_INTRODUCTION))
            .also { if (it.isNotEmpty()) movie.introduction = it }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_DETAIL)).also {
            if (it.isNotEmpty()) movie.detail = it
        }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_SUBTITLE)).also {
            if (it.isNotEmpty()) movie.subtitle = it
        }
        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_TYPE)).also {
            if (it.isNotEmpty()) movie.type = it
        }

        getValueByDsl(document, dsl.getProperty(DetailPageDsl.PROPERTY_NAME_MOVIE_CATEGORY)).also {
            if (it.isNotEmpty()) movie.category = it
        }

        process.execResult.data = movie
        process.exitNormally()
    }

    private fun execSearchPage(page: Page2, process: Process) {
        if (!isPageAvailable(page)) {
            process.exitWithError()
            interruptCurrentTask()
            return
        }

        val searchPage = SearchPage()

        // Create html document or get from cache
        val document = htmlDocumentCache[page.getUrl()]

        @Suppress("UNCHECKED_CAST")
        val dsl = SearchPageDsl(page.getDsl()!! as MutableMap<String, String>)

        val moviesElements =
            select(document, dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_LIST))
        for (moviesElement in moviesElements) {
            if (process.isDestroy) {
                interruptCurrentTask()
                return
            }
            val movie = Movie()
            movie.pluginName = page.owner!!.name

            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_COVER_IMAGE)
            ).also { if (it.isNotEmpty()) movie.coverImageUrl = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_TYPE)
            ).also { if (it.isNotEmpty()) movie.type = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_STARS)
            ).also { if (it.isNotEmpty()) movie.stars = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_DIRECTOR)
            ).also { if (it.isNotEmpty()) movie.director = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_RELEASE_TIME)
            ).also { if (it.isNotEmpty()) movie.releaseTime = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_CATEGORY)
            ).also { if (it.isNotEmpty()) movie.category = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_NAME)
            ).also { if (it.isNotEmpty()) movie.name = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_SCORE)
            ).also { if (it.isNotEmpty()) movie.score = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_DETAIL_URL)
            ).also { if (it.isNotEmpty()) movie.detailUrl = it }
            searchPage.movies.add(movie)
        }

        if (process.isDestroy) {
            interruptCurrentTask()
            return
        }

        searchPage.nextPageUrl = getValueByDsl(
            document,
            dsl.getProperty(SearchPageDsl.PROPERTY_NAME_MOVIE_NEXT_PAGE_URL)
        )
        process.execResult.data = searchPage
        process.exitNormally()
    }

    private fun execMovieCategoryPage(page: Page2, process: Process) {
        if (!isPageAvailable(page)) {
            process.exitWithError()
            interruptCurrentTask()
            return
        }

        val movieCategoryPage = MovieCategoryPage()

        // Create html document or get from cache
        val document = htmlDocumentCache[page.getUrl()]

        @Suppress("UNCHECKED_CAST")
        val dsl = MovieCategoryPageDsl(page.getDsl()!! as MutableMap<String, String>)

        val categoryGroupListElements =
            select(
                document,
                dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_CATEGORY_GROUP_LIST)
            )

        for (categoryGroupListElement in categoryGroupListElements) {
            if (process.isDestroy) {
                interruptCurrentTask()
                return
            }
            val movieCategoryGroup = MovieCategoryGroup()
            val categoryListElements =
                select(
                    categoryGroupListElement,
                    dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_CATEGORY_LIST)
                )

            for (categoryListElement in categoryListElements) {
                if (process.isDestroy) {
                    interruptCurrentTask()
                    return
                }
                val title = getValueByDsl(
                    categoryListElement,
                    dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_CATEGORY_TITLE)
                )
                val url = getValueByDsl(
                    categoryListElement,
                    dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_CATEGORY_URL)
                )
                movieCategoryGroup.movieCategories.add(MovieCategory(title, url))
            }
            movieCategoryPage.movieCategoryGroups.add(movieCategoryGroup)
        }

        val moviesElements =
            select(document, dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_LIST))
        for (moviesElement in moviesElements) {
            if (process.isDestroy) {
                interruptCurrentTask()
                return
            }

            val movie = Movie()
            getValueByDsl(
                moviesElement,
                dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_NAME)
            ).also { if (it.isNotEmpty()) movie.name = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_STATUS)
            ).also { if (it.isNotEmpty()) movie.status = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_SCORE)
            ).also { if (it.isNotEmpty()) movie.score = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_COVER_IMAGE)
            ).also { if (it.isNotEmpty()) movie.coverImageUrl = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_DETAIL_URL)
            ).also { if (it.isNotEmpty()) movie.detailUrl = it }
            getValueByDsl(
                moviesElement,
                dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_DETAIL)
            ).also { if (it.isNotEmpty()) movie.detail = it }
            movieCategoryPage.movies.add(movie)
        }

        if (process.isDestroy) {
            interruptCurrentTask()
            return
        }

        movieCategoryPage.nextPageUrl = getValueByDsl(
            document,
            dsl.getProperty(MovieCategoryPageDsl.PROPERTY_NAME_MOVIE_NEXT_PAGE_URL)
        )

        process.execResult.data = movieCategoryPage
        process.exitNormally()
    }

    private fun execHomePage(page: Page2, process: Process) {
        if (!isPageAvailable(page)) {
            process.exitWithError()
            interruptCurrentTask()
            return
        }

        val homePage = HomePage()

        // Create html document or get from cache
        val document = htmlDocumentCache[page.getUrl()]

        @Suppress("UNCHECKED_CAST")
        val dsl = HomePageDsl(page.getDsl()!! as MutableMap<String, String>)

        homePage.slideMovies = getNewMovieList(process, MOVIES_INDEX_SLIDE, page, dsl, document)
        homePage.newPlay = getNewMovieList(process, MOVIES_INDEX_NEW_PLAY, page, dsl, document)
        homePage.newMovie = getNewMovieList(process, MOVIES_INDEX_NEW_MOVIE, page, dsl, document)
        homePage.newSoap = getNewMovieList(process, MOVIES_INDEX_NEW_SOAP, page, dsl, document)
        homePage.newVariety =
            getNewMovieList(process, MOVIES_INDEX_NEW_VARIETY, page, dsl, document)
        homePage.newAnim = getNewMovieList(process, MOVIES_INDEX_NEW_ANIM, page, dsl, document)

        if (process.isDestroy) {
            interruptCurrentTask()
            return
        }
        process.execResult.data = homePage
        process.exitNormally()
    }

    private fun getNewMovieList(
        process: Process,
        moviesIndex: Int,
        page: Page2,
        dsl: HomePageDsl,
        document: Document
    ): MutableList<Movie> {
        val rMovies = mutableListOf<Movie>()

        var listDsl = ""
        var coverImageUrlDsl = ""
        var statusDsl = ""
        var nameDsl = ""
        var detailUrlDsl = ""
        var scoreDsl = ""

        when (moviesIndex) {
            MOVIES_INDEX_SLIDE -> {
                listDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_SLIDE_LIST)
                coverImageUrlDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_SLIDE_COVER_IMAGE)
                statusDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_SLIDE_STATUS)
                detailUrlDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_SLIDE_DETAIL_URL)
            }
            MOVIES_INDEX_NEW_PLAY -> {
                listDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_PLAY_LIST)
                coverImageUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_PLAY_COVER_IMAGE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_COVER_IMAGE)
                    }
                statusDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_PLAY_STATUS).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_STATUS)
                    }
                detailUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_PLAY_DETAIL_URL).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_DETAIL_URL)
                    }
                nameDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_PLAY_NAME).trim().ifEmpty {
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_NAME)
                }
                scoreDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_PLAY_SCORE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_SCORE)
                    }
            }
            MOVIES_INDEX_NEW_MOVIE -> {
                listDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_MOVIE_LIST)
                coverImageUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_MOVIE_COVER_IMAGE).trim()
                        .ifEmpty {
                            dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_COVER_IMAGE)
                        }
                statusDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_MOVIE_STATUS).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_STATUS)
                    }
                detailUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_MOVIE_DETAIL_URL).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_DETAIL_URL)
                    }
                nameDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_MOVIE_NAME).trim().ifEmpty {
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_NAME)
                }
                scoreDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_MOVIE_SCORE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_SCORE)
                    }
            }
            MOVIES_INDEX_NEW_SOAP -> {
                listDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_SOAP_LIST)
                coverImageUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_SOAP_COVER_IMAGE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_COVER_IMAGE)
                    }
                statusDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_SOAP_STATUS).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_STATUS)
                    }
                detailUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_SOAP_DETAIL_URL).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_DETAIL_URL)
                    }
                nameDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_SOAP_NAME).trim().ifEmpty {
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_NAME)
                }
                scoreDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_SOAP_SCORE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_SCORE)
                    }
            }
            MOVIES_INDEX_NEW_VARIETY -> {
                listDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_VARIETY_LIST)
                coverImageUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_VARIETY_COVER_IMAGE).trim()
                        .ifEmpty {
                            dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_COVER_IMAGE)
                        }
                statusDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_VARIETY_STATUS).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_STATUS)
                    }
                detailUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_VARIETY_DETAIL_URL).trim()
                        .ifEmpty {
                            dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_DETAIL_URL)
                        }
                nameDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_VARIETY_NAME).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_NAME)
                    }
                scoreDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_VARIETY_SCORE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_SCORE)
                    }
            }
            MOVIES_INDEX_NEW_ANIM -> {
                listDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_ANIM_LIST)
                coverImageUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_ANIM_COVER_IMAGE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_COVER_IMAGE)
                    }
                statusDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_ANIM_STATUS).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_STATUS)
                    }
                detailUrlDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_ANIM_DETAIL_URL).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_DETAIL_URL)
                    }
                nameDsl = dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_ANIM_NAME).trim().ifEmpty {
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_NAME)
                }
                scoreDsl =
                    dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_ANIM_SCORE).trim().ifEmpty {
                        dsl.getProperty(HomePageDsl.PROPERTY_NAME_NEW_COMMON_SCORE)
                    }
            }
        }

        val listElements = select(document, listDsl)
        for (slideElement in listElements) {
            if (process.isDestroy) {
                interruptCurrentTask()
                return mutableListOf()
            }
            val movie = Movie()
            movie.pluginName = page.owner!!.name

            if (coverImageUrlDsl.trim().isNotEmpty()) {
                getValueByDsl(slideElement, coverImageUrlDsl).also {
                    if (it.isNotEmpty()) movie.coverImageUrl = it
                }
            }

            if (statusDsl.trim().isNotEmpty()) {
                getValueByDsl(slideElement, statusDsl).also {
                    if (it.isNotEmpty()) movie.status = it
                }
            }

            if (detailUrlDsl.trim().isNotEmpty()) {
                getValueByDsl(slideElement, detailUrlDsl).also {
                    if (it.isNotEmpty()) movie.detailUrl = it
                }
            }

            if (nameDsl.trim().isNotEmpty()) {
                getValueByDsl(slideElement, nameDsl).also {
                    if (it.isNotEmpty()) movie.name = it
                }
            }

            if (scoreDsl.trim().isNotEmpty()) {
                getValueByDsl(slideElement, scoreDsl).also {
                    if (it.isNotEmpty()) movie.score = it
                }
            }

            rMovies.add(movie)
        }

        return rMovies
    }

    companion object {
        private const val MOVIES_INDEX_SLIDE = 0
        private const val MOVIES_INDEX_NEW_PLAY = 1
        private const val MOVIES_INDEX_NEW_MOVIE = 2
        private const val MOVIES_INDEX_NEW_SOAP = 3
        private const val MOVIES_INDEX_NEW_VARIETY = 4
        private const val MOVIES_INDEX_NEW_ANIM = 5
    }
}