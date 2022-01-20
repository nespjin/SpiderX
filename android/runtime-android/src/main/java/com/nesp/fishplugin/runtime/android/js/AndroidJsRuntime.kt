package com.nesp.fishplugin.runtime.android.js

import android.content.Context
import android.os.CancellationSignal
import android.os.SystemClock
import android.webkit.WebView
import com.google.gson.Gson
import com.nesp.fishplugin.core.data.Page
import com.nesp.fishplugin.core.data.Plugin
import com.nesp.fishplugin.runtime.IRuntimeTask
import com.nesp.fishplugin.runtime.Process
import com.nesp.fishplugin.runtime.js.JsRuntime
import com.nesp.fishplugin.runtime.movie.MoviePage
import com.nesp.fishplugin.runtime.movie.PAGE_ID_CATEGORY
import com.nesp.fishplugin.runtime.movie.data.HomePage
import com.nesp.fishplugin.runtime.movie.data.Movie
import com.nesp.fishplugin.runtime.movie.data.MovieCategoryPage
import com.nesp.fishplugin.runtime.movie.data.SearchPage
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/1/20 1:33
 * Description:
 **/
class AndroidJsRuntime(context: Context) : JsRuntime() {

    private val context = context.applicationContext

    override fun exec(page: Page, vararg parameters: Any?): Process {
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
                    override fun onPageStart() {
                        runtimeTaskListener?.onPageStart()
                    }

                    override fun onShouldInterceptRequest(url: String) {
                        runtimeTaskListener?.onShouldInterceptRequest(url)
                    }

                    override fun onReceiveError(error: String) {
                        runtimeTaskListener?.onReceiveError(error)
                    }

                    override fun onReceivePage(pageJson: String) {
                        runtimeTaskListener?.onReceivePage(pageJson)
                        val gson = Gson()
                        when {
                            page.id == MoviePage.HOME.id -> {
                                process.execResult.data =
                                    gson.fromJson(pageJson, HomePage::class.java)
                            }
                            page.id.startsWith(PAGE_ID_CATEGORY) -> {
                                process.execResult.data =
                                    gson.fromJson(pageJson, MovieCategoryPage::class.java)
                            }
                            page.id == MoviePage.SEARCH.id -> {
                                process.execResult.data =
                                    gson.fromJson(pageJson, SearchPage::class.java)
                            }
                            page.id == MoviePage.DETAIL.id -> {
                                process.execResult.data = gson.fromJson(pageJson, Movie::class.java)
                            }
                        }

                        process.exitNormally()
                    }

                    override fun onPageFinished() {
                        runtimeTaskListener?.onPageFinished()
                    }

                    override fun onTimeout() {
                        runtimeTaskListener?.onTimeout()
                        process.exitWithError()
                    }

                    override fun onPrintHtml(html: String) {
                        if (html.isNotEmpty()) {
                            htmlDocumentStringCache.put(Plugin.removeReqPrefix(page.url), html)
                        }
                    }
                }
            }

            override fun run(webView: WebView) {
                val url = page.url
                this.js = page.js

                val realUrl = Plugin.removeReqPrefix(url)
                val realUrlObj = URL(realUrl)

                val s = htmlDocumentStringCache[realUrl]
                if (!s.isNullOrEmpty()) {
                    // Load from cache
                    webView.loadDataWithBaseURL(
                        "${realUrlObj.protocol}://${realUrlObj.host}", s,
                        "text/html",
                        "utf-8",
                        null
                    )
                    return
                }

                if (Plugin.isPostReq(url)) {
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
                    webView.postUrl(realUrl, query.toByteArray(StandardCharsets.UTF_8))
                } else {
                    // Get
                    webView.loadUrl(realUrl)
                }
            }
        })
        return process
    }

    override fun runTask(task: IRuntimeTask) {
        if (task is AndroidJsRuntimeTask) {
            super.runTask(task)
            task.run()
        }
    }

    /**
     * may blocking thread
     */
    @Synchronized
    override fun shutdown() {
        tasks.forEach {
            if (it is AndroidJsRuntimeTask) {
                it.awaitFinish()
                it.destroy()
            }
        }
    }

    @Synchronized
    override fun shutdownNow() {
        tasks.forEach {
            if (it is AndroidJsRuntimeTask) {
                it.destroy()
            }
        }
    }

    private var _isTerminated = false

    @Synchronized
    override fun awaitTermination(timeout: Long, unit: TimeUnit) {
        val beginTimeMillis = System.currentTimeMillis()
        val cancellationSignal = CancellationSignal()
        thread(isDaemon = true) {
            while (true) {
                if (System.currentTimeMillis() - beginTimeMillis >=
                    TimeUnit.MILLISECONDS.convert(timeout, unit)
                ) {
                    cancellationSignal.cancel()
                    break
                }

                if (cancellationSignal.isCanceled) {
                    break
                }

                SystemClock.sleep(50)
            }
        }
        tasks.forEach {
            if (it is AndroidJsRuntimeTask) {
                it.awaitFinish(cancellationSignal)
                it.destroy()
            }
        }
        _isTerminated = true
        cancellationSignal.cancel()
    }

    @Synchronized
    override fun isTerminated(): Boolean {
        return _isTerminated
    }

}