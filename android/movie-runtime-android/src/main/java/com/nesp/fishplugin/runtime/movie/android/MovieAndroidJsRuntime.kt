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

package com.nesp.fishplugin.runtime.movie.android

import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.google.gson.Gson
import com.nesp.fishplugin.core.data.Page2
import com.nesp.fishplugin.core.data.Plugin2
import com.nesp.fishplugin.runtime.Process
import com.nesp.fishplugin.runtime.android.js.AndroidJsRuntime
import com.nesp.fishplugin.runtime.android.js.AndroidJsRuntimeTask
import com.nesp.fishplugin.runtime.android.js.AndroidJsRuntimeTaskListener
import com.nesp.fishplugin.runtime.movie.MOVIE_PAGE_ID_CATEGORY
import com.nesp.fishplugin.runtime.movie.MoviePage
import com.nesp.fishplugin.runtime.movie.data.HomePage
import com.nesp.fishplugin.runtime.movie.data.Movie
import com.nesp.fishplugin.runtime.movie.data.MovieCategoryPage
import com.nesp.fishplugin.runtime.movie.data.SearchPage
import java.lang.Exception
import java.net.URL
import java.nio.charset.MalformedInputException
import java.nio.charset.StandardCharsets

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/21 1:13
 * Description:
 **/
class MovieAndroidJsRuntime(context: Context) : AndroidJsRuntime(context) {

    private val context = context.applicationContext
    private val gson = Gson()

    override fun exec(page: Page2, vararg parameters: Any?): Process {
        val process = super.exec(page, parameters)
        process.onDestroyListener = object : Process.OnDestroyListener {
            override fun onDestroy() {
                interruptCurrentTask()
            }
        }

        var runtimeTaskListener: AndroidJsRuntimeTaskListener? = null

        if (!parameters.isNullOrEmpty() && parameters.first() is AndroidJsRuntimeTaskListener) {
            runtimeTaskListener = parameters.first() as AndroidJsRuntimeTaskListener
        }

        runTask(object : AndroidJsRuntimeTask(context) {
            init {
                // Bind listener
                listener = object : AndroidJsRuntimeTaskListener() {
                    override fun onPageLoadStart() {
                        runtimeTaskListener?.onPageLoadStart()
                    }

                    override fun onShouldInterceptRequest(url: String) {
                        runtimeTaskListener?.onShouldInterceptRequest(url)
                    }

                    override fun onReceiveError(error: String) {
                        runtimeTaskListener?.onReceiveError(error)
                        process.execResult.message = error
                        process.exitWithError()
                    }

                    override fun onReceivePage(pageJson: String) {
                        try {
                            val json =
                                pageJson.substring(1, pageJson.length - 1).replace("\\", "")
                            runtimeTaskListener?.onReceivePage(json)
                            when {
                                page.id == MoviePage.HOME.id -> {
                                    process.execResult.data =
                                        gson.fromJson(json, HomePage::class.java)
                                }
                                page.id.startsWith(MOVIE_PAGE_ID_CATEGORY) -> {
                                    process.execResult.data =
                                        gson.fromJson(json, MovieCategoryPage::class.java)
                                }
                                page.id == MoviePage.SEARCH.id -> {
                                    process.execResult.data =
                                        gson.fromJson(json, SearchPage::class.java)
                                }
                                page.id == MoviePage.DETAIL.id -> {
                                    val movie = gson.fromJson(json, Movie::class.java)
                                    movie.pluginId = page.owner!!.id
                                    movie.pluginName = page.owner!!.name
                                    process.execResult.data = movie
                                }
                            }
                        } catch (e: Exception) {
                            process.exitWithError()
                            return
                        }

                        process.exitNormally()
                    }

                    override fun onPageLoadFinished() {
                        runtimeTaskListener?.onPageLoadFinished()
                    }

                    override fun onTimeout() {
                        runtimeTaskListener?.onTimeout()
                        process.execResult.message = "Timeout"
                        process.exitWithError()
                    }

                    override fun onPrintHtml(html: String) {
                        runtimeTaskListener?.onPrintHtml(html)
                        if (html.isNotEmpty()) {
                            htmlDocumentStringCache.put(
                                Plugin2.removeReqPrefix(page.getUrl()),
                                html
                            )
                        }
                    }
                }
            }

            override fun run(jsEngine: WebView) {
                val url = page.getUrl()
                this.js = page.getJs()

                var realUrl = Plugin2.removeReqPrefix(url)
                val realUrlObj = URL(realUrl)
                try {
                    realUrl = realUrlObj.protocol + "://" + realUrlObj.host
                    val path = realUrlObj.path
                    if (path != null && path.isNotEmpty()) {
                        if (!path.startsWith("/")) realUrl += "/"
                        realUrl += path
                    }
                    val query = realUrlObj.query
                    if (query != null && query.isNotEmpty()) {
                        if (!query.startsWith("?")) realUrl += "?"
                        realUrl += query
                    }
                } catch (ignored: MalformedInputException) {
                }

                val s = htmlDocumentStringCache[realUrl]
                if (!s.isNullOrEmpty()) {
                    // Load from cache
                    jsEngine.loadDataWithBaseURL(realUrl, s, "text/html", "utf-8", null)
                    return
                }

                Log.i(TAG, "run: realUrl = $realUrl")

                if (Plugin2.isPostReq(url)) {
                    // Post
                    val query = if (realUrlObj.query != null) {
                        val data: MutableMap<String, String> = hashMapOf()
                        val kvPairs = realUrlObj.query.split("&")
                        for (kvPair in kvPairs) {
                            val kvArray = kvPair.split("=")
                            data[kvArray[0]] = kvArray[1]
                        }
                        realUrlObj.query
                    } else ""
                    jsEngine.postUrl(realUrl, query.toByteArray(StandardCharsets.UTF_8))
                } else {
                    // Get
                    jsEngine.loadUrl(realUrl)
                }
            }
        })
        return process
    }

    companion object {

        private const val TAG = "MovieAndroidJsRuntime"

    }
}