package com.nesp.fishplugin.runtime.movie.javafx;

import com.google.gson.Gson;
import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.core.data.Page2;
import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.runtime.Process;
import com.nesp.fishplugin.runtime.javafx.js.JavaFxJsRuntime;
import com.nesp.fishplugin.runtime.javafx.js.JavaFxJsRuntimeTask;
import com.nesp.fishplugin.runtime.javafx.js.JavaFxJsRuntimeTaskListener;
import com.nesp.fishplugin.runtime.movie.MoviePage;
import com.nesp.fishplugin.runtime.movie.MoviePageKt;
import com.nesp.fishplugin.runtime.movie.data.HomePage;
import com.nesp.fishplugin.runtime.movie.data.Movie;
import com.nesp.fishplugin.runtime.movie.data.MovieCategoryPage;
import com.nesp.fishplugin.runtime.movie.data.SearchPage;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class MovieJavaFxJsRuntime extends JavaFxJsRuntime {

    private final Gson gson = new Gson();
    private final Logger logger = LogManager.getLogger(MovieJavaFxJsRuntime.class);

    private int deviceType = Environment.getShared().getDeviceType();

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    @Override
    public Process exec(Page2 page, Object... parameters) {
        Process process = super.exec(page, parameters);
        Process.OnDestroyListener onDestroyListener = new Process.OnDestroyListener() {
            @Override
            public void onDestroy() {
                interruptCurrentTask();
            }
        };
        process.setOnDestroyListener(onDestroyListener);

        JavaFxJsRuntimeTaskListener runtimeTaskListener = null;

        if (parameters != null && parameters.length > 0
                && parameters[0] instanceof JavaFxJsRuntimeTaskListener) {
            runtimeTaskListener = (JavaFxJsRuntimeTaskListener) parameters[0];
        }

        JavaFxJsRuntimeTaskListener finalRuntimeTaskListener = runtimeTaskListener;
        runTask(new JavaFxJsRuntimeTask() {
            {
                setDeviceType(MovieJavaFxJsRuntime.this.getDeviceType());

                // Bind listener
                setListener(new JavaFxJsRuntimeTaskListener() {
                    @Override
                    public void onPageLoadStart() {
                        super.onPageLoadStart();
                        if (finalRuntimeTaskListener != null) {
                            finalRuntimeTaskListener.onPageLoadStart();
                        }
                    }

                    @Override
                    public void onShouldInterceptRequest(String url) {
                        super.onShouldInterceptRequest(url);
                        if (finalRuntimeTaskListener != null) {
                            finalRuntimeTaskListener.onShouldInterceptRequest(url);
                        }
                    }

                    @Override
                    public void onReceiveError(String error) {
                        super.onReceiveError(error);
                        if (finalRuntimeTaskListener != null) {
                            finalRuntimeTaskListener.onReceiveError(error);
                        }
                        process.getExecResult().setMessage(error);
                        System.out.println(MovieJavaFxJsRuntime.class.getName() + " " + error);
                        process.exitWithError();
                    }

                    @Override
                    public void onReceivePage(String pageJson) {
                        super.onReceivePage(pageJson);
                        try {
                            if (finalRuntimeTaskListener != null) {
                                finalRuntimeTaskListener.onReceivePage(pageJson);
                            }
                            if (MoviePage.HOME.getId().equals(page.getId())) {
                                process.getExecResult().setData(gson.fromJson(pageJson, HomePage.class));
                            } else if (page.getId().startsWith(MoviePageKt.MOVIE_PAGE_ID_CATEGORY)) {
                                process.getExecResult().setData(gson.fromJson(pageJson, MovieCategoryPage.class));
                            } else if (MoviePage.SEARCH.getId().equals(page.getId())) {
                                process.getExecResult().setData(gson.fromJson(pageJson, SearchPage.class));
                            } else if (MoviePage.DETAIL.getId().equals(page.getId())) {
                                process.getExecResult().setData(gson.fromJson(pageJson, Movie.class));
                            }
                        } catch (Exception e) {
                            process.exitWithError();
                            return;
                        }
                        process.exitNormally();
                    }

                    @Override
                    public void onPageLoadFinished() {
                        super.onPageLoadFinished();
                        if (finalRuntimeTaskListener != null) {
                            finalRuntimeTaskListener.onPageLoadFinished();
                        }
                    }

                    @Override
                    public void onTimeout() {
                        super.onTimeout();
                        if (finalRuntimeTaskListener != null) {
                            finalRuntimeTaskListener.onTimeout();
                        }

                        process.getExecResult().setMessage("Timeout");
                        System.out.println(MovieJavaFxJsRuntime.class.getName() + " " + "Timeout");
                        process.exitWithError();
                    }

                    @Override
                    public void onPrintHtml(String html) {
                        super.onPrintHtml(html);
                        if (finalRuntimeTaskListener != null) {
                            finalRuntimeTaskListener.onPrintHtml(html);
                        }
                        if (!html.isEmpty()) {
                            getHtmlDocumentStringCache().put(Plugin.removeReqPrefix(page.getUrl()), html);
                        }
                    }
                });
            }

            @Override
            public void run(WebView webView) {
                super.run(webView);
                String url = page.getUrl();
                setJs(page.getJs());

                String realUrl = Plugin.removeReqPrefix(url);
                URL realUrlObj = null;
                try {
                    realUrlObj = new URL(realUrl);
                    realUrl = realUrlObj.getProtocol() + "://" + realUrlObj.getHost();
                    String path = realUrlObj.getPath();
                    if (path != null && !path.isEmpty()) {
                        if (!path.startsWith("/")) realUrl += "/";
                        realUrl += path;
                    }
                    String query = realUrlObj.getQuery();
                    if (query != null && !query.isEmpty()) {
                        if (!query.startsWith("?")) realUrl += "?";
                        realUrl += query;
                    }
                } catch (MalformedURLException ignored) {
                }

                String s = getHtmlDocumentStringCache().get(realUrl);
                if (s != null && !s.isEmpty()) {
                    webView.getEngine().load(realUrl);
                    return;
                }

                System.out.println("real url = " + realUrl);

                if (Plugin.isPostReq(url)) {
//                    Map<String, String> data = new HashMap<>();
//                    realUrlObj.getQuery().split("&")
                    try {
                        webView.getEngine().load(realUrl);
                    } catch (Exception e) {
                        logger.error("error when run ", e);
                        System.out.println(MovieJavaFxJsRuntime.class.getName() + " " + e.getMessage());
                        process.exitWithError();
                    }
                } else {
                    try {
                        webView.getEngine().load(realUrl);
                    } catch (Exception e) {
                        logger.error("error when run ", e);
                        System.out.println(MovieJavaFxJsRuntime.class.getName() + " " + e.getMessage());
                        process.exitWithError();
                    }
                }
            }
        });

        return process;
    }
}
