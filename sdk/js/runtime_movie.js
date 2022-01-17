/**
 * 小丑鱼影视 Runtime SDK
 */

// include path:./runtime_core
// delete line
import Runtime from './runtime_core';

// Data Struct

/**
 * 
 * @returns 创建一个HomePage对象
 */
Runtime.prototype.createHomePage = function () {
    return {

    }
};

/**
 * 
 * @returns 创建一个CategoryPage对象
 */
Runtime.prototype.createCategoryPage = function () {
    return {

    }
};

/**
 * 
 * @returns 创建一个SearchPage对象
 */
Runtime.prototype.createSearchPage = function () {
    return {

    }
};

/**
 * 
 * @returns 创建一个DetailPage对象
 */
Runtime.prototype.createDetailPage = function () {
    return {

    }
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