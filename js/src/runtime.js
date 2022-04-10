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

/**
 * Base Runtime SDK.
 * @constructor
 */
function Runtime() {

    prototype = {
        /**
         *
         * @returns {number} Api版本
         */
        getApiLevel: function () {
            return runtimeNative.getApiLevel();
        },

        /**
         *
         * @returns {number} Runtime 版本号
         */
        getVersionCode: function () {
            return runtimeNative.getVersionCode();
        },

        /**
         *
         * @returns {string} Runtime 版本名
         */
        getVersionName: function () {
            return runtimeNative.getVersionName();
        },

        /**
         *
         * @returns {string} Runtime build号
         */
        getBuild: function () {
            return runtimeNative.getBuild();
        },

        /**
         *
         * @returns {number} 设备类型: 0: 手机 1: 平板 2: 桌面
         */
        getDeviceType: function () {
            return runtimeNative.getDeviceType();
        },

        /**
         *
         * @returns {boolean} 是否是手机
         */
        isMobilePhone: function () {
            return runtimeNative.isMobilePhone();
        },

        /**
         *
         * @returns {boolean} 是否是平板
         */
        isTable: function () {
            return runtimeNative.isTable();
        },

        /**
         *
         * @returns {boolean} 是否是桌面
         */
        isDesktop: function () {
            return runtimeNative.isDesktop();
        },
    };
}

/**
 * 小丑鱼影视 Runtime SDK
 */

// Data Struct

/**
 *
 * @returns {Object} 创建Episode对象
 */
Runtime.prototype.createEpisode = function () {
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
Runtime.prototype.createPlayLine = function () {
    return {
        "title": "",
        "episodes": []
    }
}

/**
 *
 * @returns {Object} 创建Movie对象
 */
Runtime.prototype.createMovie = function () {
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
Runtime.prototype.createMovieCategory = function () {
    return {
        "title": "",
        "pageUrl": ""
    }
}

/**
 *
 * @returns {Object} 创建MovieCategoryGroup对象
 */
Runtime.prototype.createMovieCategoryGroup = function () {
    return {
        "title": "",
        "movieClasses": []
    }
}

/**
 *
 * @returns 创建一个HomePage对象
 */
Runtime.prototype.createHomePage = function () {
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
Runtime.prototype.createCategoryPage = function () {
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
Runtime.prototype.createSearchPage = function () {
    return {
        "movies": [],
        "nextPageUrl": "",
    }
};

/**
 *
 * @returns 创建一个DetailPage对象
 */
Runtime.prototype.createDetailPage = function () {
    return window.runtime.createMovie();
};

Runtime.prototype.sendPage = function (page) {
    runtimeNative.sendPage2Platform(JSON.stringify(page));
}

Runtime.prototype.sendError = function (errorMsg) {
    runtimeNative.sendError2Platform(errorMsg);
}

Runtime.prototype.tryRun = function (method) {
    try {
        method();
    } catch (e) {
        runtime.sendError(e.toString());
    }
}

window.runtime = new Runtime();

/**
 * 初始化JSRuntime 
 */
function JsRuntime_Initialize() {

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


