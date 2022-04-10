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

package com.nesp.fishplugin.runtime.javafx.js;

import com.google.gson.Gson;
import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.runtime.CancellationSignal;
import com.nesp.fishplugin.runtime.js.JsRuntimeTask;
import com.nesp.fishplugin.tools.code.JsMinifier;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.security.GeneralSecurityException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaFxJsRuntimeTask extends JsRuntimeTask<WebView> {

    private final Logger logger = LogManager.getLogger(JavaFxJsRuntimeTask.class);
    private boolean isLoading = false;

    private synchronized void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private Timer timeoutWatcherTimer = null, loadTimer = null;

    private boolean isLoadFinished = false;

    private void setLoadFinished(boolean isLoadFinished) {
        this.isLoadFinished = isLoadFinished;
    }

    public boolean isLoadFinished() {
        return this.isLoadFinished;
    }

    private final AtomicBoolean isReceivePageOrError = new AtomicBoolean(false);

    public void setReceivePageOrError(boolean receivePageOrError) {
        isReceivePageOrError.set(receivePageOrError);
    }

    public boolean isReceivePageOrError() {
        return isReceivePageOrError.get();
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

    private int deviceType = Environment.getShared().getDeviceType();

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public final JavaFxJsRuntimeInterfaceImpl javaFxJsRuntimeInterface = new JavaFxJsRuntimeInterfaceImpl();

    public class JavaFxJsRuntimeInterfaceImpl extends JavaFxJsRuntimeInterface {

        @Override
        public void sendPage2Platform(String page) {
            super.sendPage2Platform(page);
            setReceivePageOrError(true);
            if (listener != null) listener.onReceivePage(page);
            cancelTimer(timeoutWatcherTimer);
            timeoutWatcherTimer = null;
        }

        @Override
        public void sendError2Platform(String errorMsg) {
            super.sendError2Platform(errorMsg);
            setReceivePageOrError(true);
            cancelTimer(timeoutWatcherTimer);
            timeoutWatcherTimer = null;
            if (listener != null) listener.onReceiveError(errorMsg);
        }

        @Override
        public void printHtml(String html) {
            super.printHtml(html);
            System.out.println(html);
            if (listener != null) listener.onPrintHtml(html);
        }
    }

    @Override
    public void run() {
        if (Platform.isFxApplicationThread()) {
            throw new RuntimeException("JavaFxJsRuntimeTask can't run on JavaFxApplicationThread");
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (webView == null) {
                    webView = new WebView();
                    initWebView(webView);
                }
                JavaFxJsRuntimeTask.this.run(webView);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initWebView(WebView webView) {
        if (webView == null) return;
        var userAgent =
                "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36";
        if (getDeviceType() != Environment.DEVICE_TYPE_MOBILE_PHONE) {
            // Using PC
            userAgent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36";
        }

        System.out.println("userAgent: " + userAgent);
        WebEngine engine = webView.getEngine();
        engine.setUserAgent(userAgent);
        engine.setJavaScriptEnabled(true);
        engine.setOnError(new EventHandler<WebErrorEvent>() {
            @Override
            public void handle(WebErrorEvent event) {
                System.out.println("WebEngine Error occurs: " + event.toString());
                if (listener != null) {
                    listener.onReceiveError(event.toString());
                }
            }
        });

        ChangeListener<Worker.State> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable,
                                Worker.State oldValue, Worker.State newValue) {
                System.out.println(newValue);
//                logger.info("Worker.State changed " + newValue);
                switch (newValue) {
                    case SCHEDULED -> {
                        // on page start
                        setLoading(true);
                        setLoadFinished(false);

                        if (JavaFxJsRuntimeTask.this.listener != null) {
                            JavaFxJsRuntimeTask.this.listener.onPageLoadStart();
                        }

                        cancelTimer(loadTimer);
                        loadTimer = new Timer();
                        loadTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                synchronized (JavaFxJsRuntimeTask.this) {
                                    if (!isLoadFinished()) {
                                        setLoadFinished(true);
                                        setLoading(false);
                                        if (JavaFxJsRuntimeTask.this.listener != null) {
                                            JavaFxJsRuntimeTask.this.listener.onPageLoadFinished();
                                        }
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                execCurrentJs();
                                            }
                                        });
                                    }
                                }

                                cancelTimer(loadTimer);
                                loadTimer = null;
                            }
                        }, 10 * 1000, 1);

                        cancelTimer(timeoutWatcherTimer);
                        timeoutWatcherTimer = new Timer();
                        timeoutWatcherTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (isReceivePageOrError()) {
                                    cancelTimer(timeoutWatcherTimer);
                                    timeoutWatcherTimer = null;
                                    return;
                                }
                                if (JavaFxJsRuntimeTask.this.listener != null) {
                                    JavaFxJsRuntimeTask.this.listener.onTimeout();
                                }
                                cancelTimer(timeoutWatcherTimer);
                                timeoutWatcherTimer = null;
                            }
                        }, getTimeout(), 1);
                    }
                    case RUNNING -> {
                        setLoading(true);
                        setLoadFinished(false);
                    }

                    case SUCCEEDED -> {
                        synchronized (JavaFxJsRuntimeTask.this) {
                            if (!isLoadFinished()) {
                                setLoadFinished(true);
                                setLoading(false);
                                if (JavaFxJsRuntimeTask.this.listener != null) {
                                    JavaFxJsRuntimeTask.this.listener.onPageLoadFinished();
                                }
                                execCurrentJs();
                            }
                        }
                    }

                    case FAILED -> {
                        setLoading(false);
                        setLoadFinished(true);
                        if (JavaFxJsRuntimeTask.this.listener != null) {
                            JavaFxJsRuntimeTask.this.listener.onReceiveError("Page load Failed:\n" + webView.getEngine().getLoadWorker().getException());
                        }
                        setReceivePageOrError(true);
                    }
                    case CANCELLED -> {
                        setLoading(false);
                        setLoadFinished(true);
                    }

                    case READY -> {

                    }
                }
            }
        };
        engine.getLoadWorker().stateProperty().addListener(listener);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            logger.error("SSLContext Failed: ", e);
        }

        /*engine.getLoadWorker().progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                if (newValue.doubleValue() == 1) {

                } else {


                }
            }
        });*/
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
        if (js == null || js.isEmpty()) {
            return "";
        }
        var jsTmp = new JsMinifier().minify(js).trim();
        jsTmp = "javascript:" + jsTmp;
        System.out.println("execJs js = " + jsTmp);
        var result = "";
        if (webView != null) {
            Object executeScriptResult = webView.getEngine().executeScript(jsTmp);
            System.out.println("executeScriptResult = " + new Gson().toJson(executeScriptResult));
            if (executeScriptResult instanceof String) {
                result = (String) executeScriptResult;
            }
        }
        return result;
    }

    @Override
    public synchronized void execCurrentJs() {

        JSObject window = (JSObject) webView.getEngine().executeScript("window");
        window.setMember("runtimeNative", javaFxJsRuntimeInterface);

        try {
            prepareJsRuntime();
            execJs(js);

            execJsRuntimeInitialize();
            Object result = execJsRuntimeLoadPage();
            cancelTimer(timeoutWatcherTimer); // never call here
            setReceivePageOrError(true);
            if (result instanceof String resStr) {
                if (resStr.isEmpty()) {
                    if (listener != null) {
                        listener.onReceiveError("Load Page Failed");
                    }
                } else {
                    if (listener != null) {
                        listener.onReceivePage(resStr);
                    }
                }
            } else {
                if (listener != null) listener.onReceiveError("Load Page Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            cancelTimer(timeoutWatcherTimer); // never call here
            setReceivePageOrError(true);
            if (listener != null) listener.onReceiveError("Load Page Failed " + e);
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return isLoading;
    }

    @Override
    public synchronized void awaitFinish() {
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
    public synchronized void awaitFinish(CancellationSignal cancellationSignal) {
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
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    webView.getEngine().getLoadWorker().cancel();
                    webView.getEngine().setJavaScriptEnabled(false);
                    webView = null;
                }
            };

            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(runnable);
            } else {
                runnable.run();
            }
        }
    }
}
