package com.nesp.fishplugin.tools.code;

import org.junit.jupiter.api.Test;

class JsMinifierTest {

    @Test
    void minify() {
        final String code = "/**\n" +
                " * Base Runtime SDK.\n" +
                " * @constructor\n" +
                " */\n" +
                "function Runtime() {\n" +
                "\n" +
                "    const RUNTIME_API_LEVEL_A = 1;\n" +
                "\n" +
                "    prototype = {\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {number} Api版本\n" +
                "         */\n" +
                "        getApiLevel: function () {\n" +
                "            return RUNTIME_API_LEVEL_A;\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {number} Runtime 版本号\n" +
                "         */\n" +
                "        getVersionCode: function () {\n" +
                "            return 1;\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {string} Runtime 版本名\n" +
                "         */\n" +
                "        getVersionName: function () {\n" +
                "            return \"1.0\";\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {string} Runtime build号\n" +
                "         */\n" +
                "        getBuild: function () {\n" +
                "            return \"\";\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {number} 设备类型: 0: 手机 1: 平板 2: 桌面\n" +
                "         */\n" +
                "        getDeviceType: function () {\n" +
                "            return 0;\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {boolean} 是否是手机\n" +
                "         */\n" +
                "        isMobilePhone: function () {\n" +
                "            return false;\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {boolean} 是否是平板\n" +
                "         */\n" +
                "        isTable: function () {\n" +
                "            return false;\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         *\n" +
                "         * @returns {boolean} 是否是桌面\n" +
                "         */\n" +
                "        isDesktop: function () {\n" +
                "            return false;\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         * 向应用端发送数据\n" +
                "         * @param type {number} 数据类型\n" +
                "         * @param data {Object} 数据\n" +
                "         */\n" +
                "        sendData: function (type, data) {\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         * 向应用端发送错误消息\n" +
                "         * @param errorMsg {string} 错误消息字符串\n" +
                "         */\n" +
                "        sendError: function (errorMsg) {\n" +
                "        },\n" +
                "\n" +
                "        /**\n" +
                "         * 尝试执行method，catch到异常后自动调用sendError发送错误信息到应用端。\n" +
                "         * @param method {function}\n" +
                "         */\n" +
                "        tryRun: function (method) {\n" +
                "            try {\n" +
                "                method();\n" +
                "            } catch (e) {\n" +
                "                this.sendError(e.toString());\n" +
                "            }\n" +
                "        }\n" +
                "    };\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 小丑鱼影视 Runtime SDK\n" +
                " */\n" +
                "\n" +
                "// Data Struct\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns {Object} 创建Episode对象\n" +
                " */\n" +
                "Runtime.prototype.createEpisode = function () {\n" +
                "    return {\n" +
                "        \"title\": \"\",\n" +
                "        \"pageUrl\": \"\",\n" +
                "        \"playUrl\": \"\"\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns {Object} 创建PlayLine对象\n" +
                " */\n" +
                "Runtime.prototype.createPlayLine = function () {\n" +
                "    return {\n" +
                "        \"title\": \"\",\n" +
                "        \"episodes\": []\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns {Object} 创建Movie对象\n" +
                " */\n" +
                "Runtime.prototype.createMovie = function () {\n" +
                "    return {\n" +
                "        \"name\": \"\",\n" +
                "        \"status\": \"\",\n" +
                "        \"score\": \"\",\n" +
                "        \"stars\": \"\",\n" +
                "        \"director\": \"\",\n" +
                "        \"area\": \"\",\n" +
                "        \"introduction\": \"\",\n" +
                "        \"releaseTime\": \"\",\n" +
                "        \"sourceName\": \"\",\n" +
                "        \"detailUrl\": \"\",\n" +
                "        \"coverImageUrl\": \"\",\n" +
                "        \"detail\": \"\",\n" +
                "        \"subtitle\": \"\",\n" +
                "        \"type\": \"\",\n" +
                "        \"category\": \"\",\n" +
                "        \"playLines\": []\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns {Object} 创建MovieCategory对象\n" +
                " */\n" +
                "Runtime.prototype.createMovieCategory = function () {\n" +
                "    return {\n" +
                "        \"title\": \"\",\n" +
                "        \"pageUrl\": \"\"\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns {Object} 创建MovieCategoryGroup对象 \n" +
                " */\n" +
                "Runtime.prototype.createMovieCategoryGroup = function () {\n" +
                "    return {\n" +
                "        \"title\": \"\",\n" +
                "        \"movieClasses\": []\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns 创建一个HomePage对象\n" +
                " */\n" +
                "Runtime.prototype.createHomePage = function () {\n" +
                "    return {\n" +
                "        \"slideMovies\": [],\n" +
                "        \"newPlay\": [],\n" +
                "        \"newMovie\": [],\n" +
                "        \"newSoap\": [],\n" +
                "        \"newVariety\": [],\n" +
                "        \"newAnim\": [],\n" +
                "    }\n" +
                "};\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns 创建一个CategoryPage对象\n" +
                " */\n" +
                "Runtime.prototype.createCategoryPage = function () {\n" +
                "    return {\n" +
                "        \"movieCategoryGroups\": [],\n" +
                "        \"movies\": [],\n" +
                "        \"nextPageUrl\": \"\",\n" +
                "    }\n" +
                "};\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns 创建一个SearchPage对象\n" +
                " */\n" +
                "Runtime.prototype.createSearchPage = function () {\n" +
                "    return {\n" +
                "        \"movies\": [],\n" +
                "        \"nextPageUrl\": \"\",\n" +
                "    }\n" +
                "};\n" +
                "\n" +
                "/**\n" +
                " * \n" +
                " * @returns 创建一个DetailPage对象\n" +
                " */\n" +
                "Runtime.prototype.createDetailPage = function () {\n" +
                "    return window.runtime.createMovie();\n" +
                "};\n" +
                "\n" +
                "/**\n" +
                " * 向应用端发送主页面数据\n" +
                " * @param {Object} homePage 主页面Object\n" +
                " */\n" +
                "Runtime.prototype.sendHomePage = function (homePage) {\n" +
                "\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 向应用端发送视频分类页面数据\n" +
                " * @param {Object} categoryPage 视频分类页面Object\n" +
                " */\n" +
                "Runtime.prototype.sendCategoryPage = function (categoryPage) {\n" +
                "\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 向应用端发送搜索页面数据\n" +
                " * @param {Object} searchPage 搜索页面Object\n" +
                " */\n" +
                "Runtime.prototype.sendSearchPage = function (searchPage) {\n" +
                "\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * 向应用端发送视频详情数据\n" +
                " * @param {Object} detailPage 视频详情页面Object\n" +
                " */\n" +
                "Runtime.prototype.sendDetailPage = function (detailPage) {\n" +
                "\n" +
                "}\n" +
                "\n" +
                "window.runtime = new Runtime();";
        final CodeMinifier minifier = new JsMinifier();
        System.out.println(minifier.minify(code));;
    }
}