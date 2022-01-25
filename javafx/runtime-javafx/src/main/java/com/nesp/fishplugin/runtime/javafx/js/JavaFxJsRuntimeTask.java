package com.nesp.fishplugin.runtime.javafx.js;

import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.runtime.CancellationSignal;
import com.nesp.fishplugin.runtime.js.JsRuntimeTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.Timer;
import java.util.TimerTask;

public class JavaFxJsRuntimeTask extends JsRuntimeTask<WebView> {

    private boolean isLoading = false;

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private Timer timeoutWatcherTimer = null, loadTimer = null;

    private boolean isLoadFinished = false;

    private void setLoadFinished(boolean isLoadFinished) {
        this.isLoadFinished = isLoadFinished;
        if (isLoadFinished) {
            if (loadTimer != null) loadTimer.cancel();
        }
    }

    public boolean isLoadFinished() {
        return this.isLoadFinished;
    }

    private String js = "";

    public void setJs(String js) {
        this.js = js;
    }

    public String getJs() {
        return js;
    }

    private long timeout = 60 * 1000L;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    private WebView webView;

    private JavaFxJsRuntimeTaskListener listener = null;

    public void setListener(JavaFxJsRuntimeTaskListener listener) {
        this.listener = listener;
    }

    private final JavaFxJsRuntimeInterface javaFxJsRuntimeInterface = new JavaFxJsRuntimeInterface() {

        @Override
        public void sendPage2Platform(String page) {
            super.sendPage2Platform(page);
            if (listener != null) listener.onReceivePage(page);
        }

        @Override
        public void sendError2Platform(String errorMsg) {
            super.sendError2Platform(errorMsg);
            if (listener != null) listener.onReceiveError(errorMsg);
        }

        @Override
        public void printHtml(String html) {
            super.printHtml(html);
            if (listener != null) listener.onPrintHtml(html);
        }
    };

    @Override
    public void run() {
        if (webView == null) {
            webView = new WebView();
            initWebView(webView);
        }
        run(webView);
    }

    private void initWebView(WebView webView) {
        if (webView == null) return;
        Environment environment = Environment.getShared();
        var userAgent =
                "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36";
        if (!environment.isMobilePhone()) {
            // Using PC
            userAgent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36";
        }

        WebEngine engine = webView.getEngine();
        engine.setUserAgent(userAgent);
        engine.setJavaScriptEnabled(true);
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("runtime", javaFxJsRuntimeInterface);

        ChangeListener<Worker.State> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable,
                                Worker.State oldValue, Worker.State newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    JSObject win = (JSObject) engine.executeScript("window");
                }
            }
        };
        engine.getLoadWorker().stateProperty().addListener(listener);

        engine.getLoadWorker().progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                if (newValue.doubleValue() == 100) {
                    if (!isLoadFinished()) execCurrentJs();
                    setLoading(false);
                    setLoadFinished(true);

                    if (JavaFxJsRuntimeTask.this.listener != null) {
                        JavaFxJsRuntimeTask.this.listener.onPageLoadFinished();
                    }
                } else {
                    setLoading(true);
                    if (newValue.doubleValue() == 0) {
                        // on page start
                        setLoading(true);
                        setLoadFinished(true);

                        if (JavaFxJsRuntimeTask.this.listener != null) {
                            JavaFxJsRuntimeTask.this.listener.onPageLoadStart();
                        }

                        if (loadTimer != null) {
                            loadTimer.cancel();
                            loadTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!isLoadFinished()) {
                                        execCurrentJs();
                                        setLoadFinished(true);
                                        setLoading(false);
                                        if (JavaFxJsRuntimeTask.this.listener != null) {
                                            JavaFxJsRuntimeTask.this.listener.onPageLoadFinished();
                                        }
                                    }

                                    cancelTimer(loadTimer);
                                    loadTimer = null;

                                    cancelTimer(timeoutWatcherTimer);
                                    timeoutWatcherTimer = null;
                                }
                            }, 10 * 1000, 1);

                            cancelTimer(timeoutWatcherTimer);
                            if (timeoutWatcherTimer != null) {
                                timeoutWatcherTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (isLoadFinished) return;
                                        if (JavaFxJsRuntimeTask.this.listener != null) {
                                            JavaFxJsRuntimeTask.this.listener.onTimeout();
                                        }
                                        cancelTimer(timeoutWatcherTimer);
                                        timeoutWatcherTimer = null;
                                    }
                                }, getTimeout(), 1);
                            }
                        }
                    }
                }
            }
        });
    }

    private void cancelTimer(Timer timeoutWatcherTimer) {
        if (timeoutWatcherTimer != null) {
            timeoutWatcherTimer.cancel();
            timeoutWatcherTimer = null;
        }
    }

    @Override
    public void interrupt() {
        destroy();
    }

    @Override
    public void run(WebView webView) {

    }

    @Override
    public Object execJs(String js) {
        var jsTmp = js.replace("\n", "").trim();
        jsTmp = "javascript:" + jsTmp;
        var result = "";
        if (webView != null) {
            Object executeScriptResult = webView.getEngine().executeScript(jsTmp);
            if (executeScriptResult instanceof String) {
                result = (String) executeScriptResult;
            }
        }
        return result;
    }

    @Override
    public void execCurrentJs() {
        prepareJsRuntime();
        execJs(js);

        execJsRuntimeInitialize();
        Object result = execJsRuntimeLoadPage();
        if (result instanceof String) {
            if (listener != null) listener.onReceivePage((String) result);
        }
    }

    @Override
    public boolean isRunning() {
        return isLoading;
    }

    @Override
    public void awaitFinish() {
        while (isLoading) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void awaitFinish(CancellationSignal cancellationSignal) {
        while (isLoading) {
            if (cancellationSignal != null) {
                cancellationSignal.throwIfCanceled();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void pauseTimers() {
        // do nothing
    }

    @Override
    public void resumeTimers() {
        // do nothing
    }

    @Override
    public void pause() {
        // do nothing
    }

    @Override
    public void resume() {
        // do nothing
    }

    @Override
    public void destroy() {
        cancelTimer(timeoutWatcherTimer);

        cancelTimer(loadTimer);

        if (webView != null) {
            webView.getEngine().getLoadWorker().cancel();
            webView.getEngine().setJavaScriptEnabled(false);
            webView = null;
        }
    }
}
