/*
 * Copyright (c) 2025.  NESP Technology.
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

package com.nesp.spiderx.runtime.javafx;

import com.google.gson.Gson;
import com.nesp.spiderx.runtime.JniWebView;
import com.nesp.spiderx.runtime.PluginManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
public class JavaFxWebView extends JniWebView implements EventHandler<WebErrorEvent>, ChangeListener<Worker.State> {
    private static final String TAG = "JavaFxWebView";
//    private static final Logger logger = LogManager.getLogger();

    private static final List<WebView> WEBVIEW_POOLS = new ArrayList<>();

    private WebView webView;
    private final ProgressListener progressListener = new ProgressListener(this);
    private final Gson gson = new Gson();

    @Override
    public void onInit() {
        if (webView != null) {
            return;
        }
        final PluginManager pluginManager = PluginManager.getInstance();

        webView = new WebView();
        webView.setVisible(false);
        webView.setPrefWidth(0);
        webView.setPrefHeight(0);
        final WebEngine engine = webView.getEngine();
        engine.setUserAgent(pluginManager.getUserAgent());
        engine.setJavaScriptEnabled(true);
        engine.setOnError(this);
        engine.getLoadWorker().stateProperty().addListener(this);

        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new EmptyX509TrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (GeneralSecurityException e) {
//            logger.error("SSLContext Failed: ", e);
        }

        engine.getLoadWorker().progressProperty().addListener(progressListener);
        WEBVIEW_POOLS.add(webView);
    }

    @Override
    public void handle(WebErrorEvent event) {
        String url = webView.getEngine().getLocation();
        url = url == null ? "" : url;
        notifyOnPageError(url, event.getMessage());
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        String url = webView.getEngine().getLocation();
        url = url == null ? "" : url;
        System.out.println("newValue = " + newValue + " changed Thread " + Thread.currentThread().getName());
        switch (newValue) {
            case READY:
                break;
            case SCHEDULED:
                notifyOnPageStarted(url);
                break;
            case RUNNING:
                break;
            case SUCCEEDED:
                notifyOnPageFinished(url);
                break;
            case CANCELLED:
                notifyOnPageCancelled(url);
                break;
            case FAILED:
                final Worker<Void> loadWorker = webView.getEngine().getLoadWorker();
                notifyOnPageError(url, loadWorker.getException().getMessage());
                break;
        }
    }

    @Override
    public void performLoadUrl(@NotNull String url) {
        webView.getEngine().load(url);
    }

    @Override
    public void performLoadData(@NotNull String data) {
        webView.getEngine().loadContent(data);
    }

    @Override
    public void performReload() {
        webView.getEngine().reload();
    }

    @Override
    @Nullable
    public String performEvaluate(@NotNull String javascript) {
        final WebEngine engine = webView.getEngine();
        JSObject window = (JSObject) engine.executeScript("window");
        if (window != null) {
            window.setMember(JniWebView.SPIDERX_RUNTIME_JAVASCRIPT_OBJECT_NAME, new JavaFxSpiderXRuntimeJavaScriptObject(this));
        }

        final Object ret = engine.executeScript(javascript);
        if (ret == null) return null;
        if (ret instanceof String) return (String) ret;
        return gson.toJson(ret);
    }

    @Override
    public void onDestroy() {
        System.out.println("JavaFxJsRuntimeTask destroy");
        if (webView != null) {
            WEBVIEW_POOLS.remove(webView);
            webView.getEngine().getLoadWorker().cancel();
            webView.getEngine().setJavaScriptEnabled(false);
            webView.getEngine().getLoadWorker().stateProperty().removeListener(this);
            webView.getEngine().getLoadWorker().progressProperty().removeListener(progressListener);
            webView = null;
        }
    }

    @Override
    public boolean isMainThread() {
        return Platform.isFxApplicationThread();
    }

    @Override
    public @NotNull ThreadFactory getMainThreadFactory() {
        return new DelegateThreadFactory(Platform::runLater);
    }


    private static class JavaFxSpiderXRuntimeJavaScriptObject implements SpiderXRuntimeJavaScriptObject {
        private final JavaFxWebView webView;

        private JavaFxSpiderXRuntimeJavaScriptObject(JavaFxWebView webView) {
            this.webView = webView;
        }

        @Override
        public void sendData(@NotNull String data) {
            String url = webView.webView.getEngine().getLocation();
            url = url == null ? "" : url;
            webView.notifyOnReceivedData(url, data);
        }

        @Override
        public void sendError(@NotNull String error) {
            String url = webView.webView.getEngine().getLocation();
            url = url == null ? "" : url;
            webView.notifyOnReceivedError(url, error);
        }
    }

    private static class ProgressListener implements ChangeListener<Number> {
        private final JavaFxWebView webView;

        private ProgressListener(JavaFxWebView webView) {
            this.webView = webView;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            System.out.println("ProgressListener Thread " + Thread.currentThread().getName());
            webView.notifyOnLoadProgress(Math.round(newValue.floatValue() * 100));
        }
    }

    private static class EmptyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
