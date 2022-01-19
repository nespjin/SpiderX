package com.nesp.fishplugin.runtime.movie.dsl

import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.runtime.Process
import com.nesp.fishplugin.runtime.dsl.DslRuntime
import com.nesp.fishplugin.runtime.movie.data.HomePage
import com.nesp.fishplugin.runtime.movie.data.Movie
import com.nesp.fishplugin.runtime.movie.data.MovieCategoryPage
import org.jsoup.nodes.Document

class MovieDSLRuntime : DslRuntime() {

    override fun exec(page: Page): Process {
        val process = Process()
        runTask {
            when (page.id) {
                PAGE_ID_HOME -> {
                    execHomePage(page, process)
                }
                PAGE_ID_CATEGORY -> {
                    execMovieCategoryPage(page, process)
                }
                else -> {
                    process.exitWithError()
                }
            }
        }
        return process
    }

    private fun execMovieCategoryPage(page: Page, process: Process) {
        if (!isPageAvailable(page)) {
            process.exitWithError()
            interruptCurrentTask()
            return
        }

        val movieCategoryPage = MovieCategoryPage()

        // Create html document or get from cache
        val document = htmlDocumentCache[page.url]

        val dsl = HomePageDsl(page.dsl!! as MutableMap<String, String>)


        process.execResult.data = movieCategoryPage
        process.exitNormally()
    }

    private fun execHomePage(page: Page, process: Process) {
        if (!isPageAvailable(page)) {
            process.exitWithError()
            interruptCurrentTask()
            return
        }

        val homePage = HomePage()

        // Create html document or get from cache
        val document = htmlDocumentCache[page.url]

        val dsl = HomePageDsl(page.dsl!! as MutableMap<String, String>)

        homePage.slideMovies = getNewMovieList(MOVIES_INDEX_SLIDE, page, dsl, document)
        homePage.newPlay = getNewMovieList(MOVIES_INDEX_NEW_PLAY, page, dsl, document)
        homePage.newMovie = getNewMovieList(MOVIES_INDEX_NEW_MOVIE, page, dsl, document)
        homePage.newSoap = getNewMovieList(MOVIES_INDEX_NEW_SOAP, page, dsl, document)
        homePage.newVariety = getNewMovieList(MOVIES_INDEX_NEW_VARIETY, page, dsl, document)
        homePage.newAnim = getNewMovieList(MOVIES_INDEX_NEW_ANIM, page, dsl, document)

        process.execResult.data = homePage
        process.exitNormally()
    }

    private fun getNewMovieList(moviesIndex: Int, page: Page, dsl: HomePageDsl, document: Document)
            : MutableList<Movie> {
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
            val movie = Movie()
            movie.sourceName = page.owner!!.name

            if (coverImageUrlDsl.trim().isNotEmpty()) {
                movie.coverImageUrl = getValueByDsl(slideElement, coverImageUrlDsl)
            }

            if (statusDsl.trim().isNotEmpty()) {
                movie.status = getValueByDsl(slideElement, statusDsl)
            }

            if (detailUrlDsl.trim().isNotEmpty()) {
                movie.detailUrl = getValueByDsl(slideElement, detailUrlDsl)
            }

            if (nameDsl.trim().isNotEmpty()) {
                movie.name = getValueByDsl(slideElement, nameDsl)
            }

            if (scoreDsl.trim().isNotEmpty()) {
                movie.score = getValueByDsl(slideElement, scoreDsl)
            }

            rMovies.add(movie)
        }

        return rMovies
    }

    companion object {
        const val PAGE_ID_HOME = "home"
        const val PAGE_ID_CATEGORY = "category"
        const val PAGE_ID_SEARCH = "search"
        const val PAGE_ID_DETAIL = "detail"

        private const val MOVIES_INDEX_SLIDE = 0
        private const val MOVIES_INDEX_NEW_PLAY = 1
        private const val MOVIES_INDEX_NEW_MOVIE = 2
        private const val MOVIES_INDEX_NEW_SOAP = 3
        private const val MOVIES_INDEX_NEW_VARIETY = 4
        private const val MOVIES_INDEX_NEW_ANIM = 5
    }
}