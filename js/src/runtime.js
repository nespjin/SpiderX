
/**
 * 初始化JSRuntime 
 */
function JsRuntime_Initialize() {

    runtime.sendPage = function (page) {
        runtime.sendPage2Platform(JSON.stringify(page));
    }

    runtime.sendError = function (errorMsg) {
        runtime.sendError2Platform(errorMsg);
    }

    runtime.tryRun = function (method) {
        try {
            method();
        } catch (e) {
            runtime.sendError(e.toString());
        }
    }

    JsRuntime_InitializeMovie();

    // Add your custom initialize....
}

/**
 * Initialize Runtime for Movie
 */
function JsRuntime_InitializeMovie() {

    runtime.createEpisode = function () {
        return {
            "title": "",
            "pageUrl": "",
            "playUrl": ""
        }
    }

    runtime.createPlayLine = function () {
        return {
            "title": "",
            "episodes": []
        }
    }

    runtime.createMovie = function () {
        return {
            "name": "",
            "status": "",
            "score": "",
            "stars": "",
            "director": "",
            "area": "",
            "introduction": "",
            "releaseTime": "",
            "sourceName": "",
            "detailUrl": "",
            "coverImageUrl": "",
            "detail": "",
            "subtitle": "",
            "type": "",
            "category": "",
            "playLines": []
        }
    }

    runtime.createMovieCategory = function () {
        return {
            "title": "",
            "pageUrl": ""
        }
    }

    runtime.createMovieCategoryGroup = function () {
        return {
            "title": "",
            "movieClasses": []
        }
    }

    runtime.createHomePage = function () {
        return {
            "slideMovies": [],
            "newPlay": [],
            "newMovie": [],
            "newSoap": [],
            "newVariety": [],
            "newAnim": [],
        }
    }

    runtime.createCategoryPage = function () {
        return {
            "movieCategoryGroups": [],
            "movies": [],
            "nextPageUrl": "",
        }
    }

    runtime.createSearchPage = function () {
        return {
            "movies": [],
            "nextPageUrl": "",
        }
    }

    runtime.createDetailPage = function () {
        return runtime.createMovie();
    }

}


