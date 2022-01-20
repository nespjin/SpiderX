/**
 * Base Runtime SDK.
 * @constructor
 */
function Runtime() {

    const RUNTIME_API_LEVEL_A = 1;

    prototype = {
        /**
         *
         * @returns {number} Api版本
         */
        getApiLevel: function () {
            return RUNTIME_API_LEVEL_A;
        },

        /**
         *
         * @returns {number} Runtime 版本号
         */
        getVersionCode: function () {
            return 1;
        },

        /**
         *
         * @returns {string} Runtime 版本名
         */
        getVersionName: function () {
            return "1.0";
        },

        /**
         *
         * @returns {string} Runtime build号
         */
        getBuild: function () {
            return "";
        },

        /**
         *
         * @returns {number} 设备类型: 0: 手机 1: 平板 2: 桌面
         */
        getDeviceType: function () {
            return 0;
        },

        /**
         *
         * @returns {boolean} 是否是手机
         */
        isMobilePhone: function () {
            return false;
        },

        /**
         *
         * @returns {boolean} 是否是平板
         */
        isTable: function () {
            return false;
        },

        /**
         *
         * @returns {boolean} 是否是桌面
         */
        isDesktop: function () {
            return false;
        },

        /**
         * 向应用端发送数据
         * @param type {number} 数据类型
         * @param data {Object} 数据
         */
        sendData: function (type, data) {
        },

        /**
         * 向应用端发送错误消息
         * @param errorMsg {string} 错误消息字符串
         */
        sendError: function (errorMsg) {
        },

        /**
         * 尝试执行method，catch到异常后自动调用sendError发送错误信息到应用端。
         * @param method {function}
         */
        tryRun: function (method) {
            try {
                method();
            } catch (e) {
                this.sendError(e.toString());
            }
        }
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

/**
 * 向应用端发送主页面数据
 * @param {Object} homePage 主页面Object
 */
Runtime.prototype.sendHomePage = function (homePage) {

}

/**
 * 向应用端发送视频分类页面数据
 * @param {Object} categoryPage 视频分类页面Object
 */
Runtime.prototype.sendCategoryPage = function (categoryPage) {

}

/**
 * 向应用端发送搜索页面数据
 * @param {Object} searchPage 搜索页面Object
 */
Runtime.prototype.sendSearchPage = function (searchPage) {

}

/**
 * 向应用端发送视频详情数据
 * @param {Object} detailPage 视频详情页面Object
 */
Runtime.prototype.sendDetailPage = function (detailPage) {

}

window.runtime = new Runtime();