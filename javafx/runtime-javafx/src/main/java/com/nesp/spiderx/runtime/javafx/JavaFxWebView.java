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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
    private static final Logger LOGGER = LogManager.getLogger(JavaFxWebView.class);

    private WebView webView;
    private final ProgressListener progressListener = new ProgressListener(this);
    private final Gson gson = new Gson();
    private final JavaBridge javaBridge = new JavaBridge();
    private static final String preloadJavaScript = """
            // 创建请求代理
            function createRequestProxy() {
                java.print('拦截请求');
            
                document.addEventListener('beforeload', (e) => {
                    const targetUrl = e.target.src || e.target.href;
                    java.print('Loading ' + targetUrl);
                }, true);
            
                document.addEventListener('fetch', (e) => {
                    const targetUrl = e.request.url;
                    java.print('Loading ' + targetUrl);
                }, true);
            
                // 拦截 fetch
                const originalFetch = window.fetch;
                window.fetch = async function(...args) {
                    console.log('拦截请求:', args[0]);
                    java.print('拦截请求 ' + args[0]);
            
                    // 可以在这里修改请求参数
                    const modifiedArgs = modifyRequestArgs(args);
            
                    try {
                        const response = await originalFetch.apply(this, modifiedArgs);
                        console.log('请求响应:', response);
                        return response;
                    } catch (error) {
                        console.error('请求失败:', error);
                        throw error;
                    }
                };
            
                // 拦截 XMLHttpRequest
                const originalXHROpen = XMLHttpRequest.prototype.open;
                XMLHttpRequest.prototype.open = function(method, url, ...rest) {
                    console.log('XHR 请求:', method, url);
                    java.print('XHR 请求: ' + ' ' + method + ' ' + url);
                    this._url = url; // 保存 URL 供后续使用
                    return originalXHROpen.apply(this, [method, url, ...rest]);
                };
            }
            
            createRequestProxy();
            java.print('createRequestProxy finished');
            """;

    @Override
    public void onInit() {
        if (webView != null) {
            return;
        }

        initURL();

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
        engine.getLoadWorker().progressProperty().addListener(progressListener);
        LOGGER.trace("JavaFxWebView onInit finished");
    }

    private static void initURL() {
        // setupURLStreamHandler();

        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new EmptyX509TrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (GeneralSecurityException e) {
            LOGGER.error("SSLContext Failed: ", e);
        }
    }

    private static void setupURLStreamHandler() {
        try {
            URL.setURLStreamHandlerFactory(protocol -> {
                LOGGER.trace("The protocol is {}", protocol);
                return "https".equals(protocol) || "http".equals(protocol) ?
                        new WebURLStreamHandler() : null;
            });
        } catch (Exception ignore) {
        }
    }

    @Override
    public void handle(WebErrorEvent event) {
        String url = webView.getEngine().getLocation();
        url = url == null ? "" : url;
        notifyOnPageError(url, event.getMessage());
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        final String location = webView.getEngine().getLocation();
        final String url = location == null ? "" : location;
        LOGGER.trace("newValue = {} changed Thread {}", newValue.toString(), Thread.currentThread().getName());
        switch (newValue) {
            case READY:
                break;
            case SCHEDULED:
                final WebEngine engine = webView.getEngine();
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("java", javaBridge);
                engine.executeScript(preloadJavaScript);
                notifyOnPageStarted(url);
                break;
            case RUNNING:
                break;
            case SUCCEEDED:
                final String document;
                try {
                    document = formatHtmlDocument(webView.getEngine().getDocument());
                } catch (TransformerException e) {
                    throw new RuntimeException(e);
                }
                notifyOnPageFinished(url, document);
                break;
            case CANCELLED:
                // notifyOnPageCancelled(url);
                break;
            case FAILED:
                final Worker<Void> loadWorker = webView.getEngine().getLoadWorker();
                notifyOnPageError(url, loadWorker.getException().getMessage());
                break;
        }
    }

    private String formatHtmlDocument(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        return writer.toString();
    }

    @Override
    public void performLoadUrl(@NotNull String url) {
        LOGGER.trace("JavaFxWebView performLoadUrl {}", url);
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
        LOGGER.trace("{} evaluated result is  {}", javascript, ret);
        if (ret == null) return null;
        if (ret instanceof String) return (String) ret;
        return gson.toJson(ret);
    }

    @Override
    public void onDestroy() {
        LOGGER.trace("JavaFxWebView onDestroy");
        if (webView != null) {
            webView.getEngine().getLoadWorker().cancel();
            webView.getEngine().setJavaScriptEnabled(false);
            webView.getEngine().getLoadWorker().stateProperty().removeListener(this);
            webView.getEngine().getLoadWorker().progressProperty().removeListener(progressListener);
            webView = null;
        }
        LOGGER.trace("JavaFxWebView onDestroy finished");
    }

    @Override
    public void dispatchMainThread(@NotNull Runnable task) {
        Platform.runLater(task);
    }

    @Override
    public boolean isMainThread() {
        return Platform.isFxApplicationThread();
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
            final String url = webView.webView.getEngine().getLocation();
            webView.notifyOnLoadProgress(url, Math.round(newValue.floatValue() * 100));
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

    public static class JavaBridge {
        public void print(String message) {
            System.out.println("From JavaScript: " + message);
        }
    }

    private static class WebURLStreamHandler extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            final String originUrl = url.toString();

            LOGGER.trace("Loading url:{}", originUrl);

            URLConnection originalConn = createSecureConnection(url);
            originalConn.setRequestProperty("Cache-Control", "no-cache");

            return originalConn;
        }
    }

    /**
     * 创建 HTTP/HTTPS 连接（HTTPS 信任所有证书，测试用）
     */
    private static URLConnection createSecureConnection(URL url) throws IOException {
        URLConnection conn;
        if ("https".equals(url.getProtocol())) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
            // HTTPS 证书配置（测试环境专用，生产需替换为合法证书）
            final SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{new EmptyX509TrustManager()}, new SecureRandom());
                httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
            } catch (GeneralSecurityException e) {
                LOGGER.error("SSLContext Failed: ", e);
            }
            httpsConn.setHostnameVerifier((hostname, session) -> true);
            conn = httpsConn;
        } else {
            conn = url.openConnection();
        }
        return conn;
    }

}

