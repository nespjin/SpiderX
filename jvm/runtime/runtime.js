/**
 * Base Runtime SDK.
 * @constructor
 */
function JsRuntime_Initialize() {

    window.runtime = {

    };

    window.runtime.getApiLevel = function () {
        return runtimeNative.getApiLevel();
    }

    window.runtime.getVersionCode = function () {
        return runtimeNative.getVersionCode();
    }

    window.runtime.getVersionName = function () {
        return runtimeNative.getVersionName();
    }

    window.runtime.getBuild = function () {
        return runtimeNative.getBuild();
    }

    window.runtime.getDeviceType = function () {
        return runtimeNative.getDeviceType();
    }

    window.runtime.isMobilePhone = function () {
        return runtimeNative.isMobilePhone();
    }

    window.runtime.isTable = function () {
        return runtimeNative.isTable();
    }

    window.runtime.isDesktop = function () {
        return runtimeNative.isDesktop();
    }

    window.runtime.printHtml = function (html) {
        runtimeNative.printHtml(html);
    }

    window.runtime.sendPage = function (page) {
        runtimeNative.sendPage2Platform(JSON.stringify(page));
    }

    window.runtime.sendError = function (errorMsg) {
        runtimeNative.sendError2Platform(errorMsg);
    }

    window.runtime.tryRun = function (method) {
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

    window.runtime.createEpisode = function () {
        return {
            "title": "",
            "pageUrl": "",
            "playUrl": ""
        }
    }

    /**
     *
     * @returns {Object} 创建PlayLine对象
     */
    window.runtime.createPlayLine = function () {
        return {
            "title": "",
            "episodes": []
        }
    }

    /**
     *
     * @returns {Object} 创建Movie对象
     */
    window.runtime.createMovie = function () {
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

    /**
     *
     * @returns {Object} 创建MovieCategory对象
     */
    window.runtime.createMovieCategory = function () {
        return {
            "title": "",
            "pageUrl": ""
        }
    }

    /**
     *
     * @returns {Object} 创建MovieCategoryGroup对象
     */
    window.runtime.createMovieCategoryGroup = function () {
        return {
            "title": "",
            "movieCategories": []
        }
    }

    /**
     *
     * @returns 创建一个HomePage对象
     */
    window.runtime.createHomePage = function () {
        return {
            "slideMovies": [],
            "newPlay": [],
            "newMovie": [],
            "newSoap": [],
            "newVariety": [],
            "newAnim": [],
        }
    };

    /**
     *
     * @returns 创建一个CategoryPage对象
     */
    window.runtime.createCategoryPage = function () {
        return {
            "movieCategoryGroups": [],
            "movies": [],
            "nextPageUrl": "",
        }
    };

    /**
     *
     * @returns 创建一个SearchPage对象
     */
    window.runtime.createSearchPage = function () {
        return {
            "movies": [],
            "nextPageUrl": "",
        }
    };

    /**
     *
     * @returns 创建一个DetailPage对象
     */
    window.runtime.createDetailPage = function () {
        return window.runtime.createMovie();
    };

}


