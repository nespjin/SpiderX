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

package com.nesp.fishplugin.runtime.js

import com.nesp.fishplugin.runtime.CancellationSignal
import com.nesp.fishplugin.runtime.IRuntimeTask
import com.nesp.fishplugin.tools.code.JsMinifier

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 11:44
 * Description:
 **/
interface IJsRuntimeTask<JsEngine> : IRuntimeTask {

    fun run()

    fun run(jsEngine: JsEngine)

    fun execJs(js: String): Any?

    fun isRunning(): Boolean

    fun awaitFinish()

    fun awaitFinish(cancellationSignal: CancellationSignal)

    fun pauseTimers()

    fun resumeTimers()

    fun pause()

    fun resume()

    fun destroy()

    fun prepareJsRuntime() {
        execJs(JsMinifier().minify(JS).trim())
    }

    fun execJsRuntimeLoadPage(): Any? {
        return execJs("$JS_RUNTIME_FUNCTION_NAME_LOAD_PAGE();")
    }

    fun execJsRuntimeInitialize() {
        execJs("$JS_RUNTIME_FUNCTION_NAME_INITIALIZE();")
    }

    fun execCurrentJs()

    companion object {
        const val JS_RUNTIME_FUNCTION_NAME_LOAD_PAGE = "JsRuntime_LoadPage"
        const val JS_RUNTIME_FUNCTION_NAME_INITIALIZE = "JsRuntime_Initialize"

        private const val JS = """
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
        """
    }
}