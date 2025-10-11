package com.nesp.spiderx.runtime.javafx;

import com.nesp.spiderx.runtime.JniWebView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefAuthCallback;
import org.cef.callback.CefCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.handler.CefRequestHandler;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.EnumProgress;
import me.friwi.jcefmaven.IProgressHandler;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
public class JavaFxJcefWebView extends JniWebView {
    private static final Logger LOGGER = LogManager.getLogger(JavaFxJcefWebView.class);

    private static final String TAG = "JavaFxJcefWebView";

    private CefApp cefApp;
    private CefClient cefClient;
    private CefBrowser cefBrowser;

    @Override
    public void onInit() {
        // Create a new CefAppBuilder instance
        CefAppBuilder builder = new CefAppBuilder();

        // Configure the builder instance
        builder.setInstallDir(new File("jcef-bundle")); // Default
        builder.setProgressHandler(new IProgressHandler() {
            @Override
            public void handleProgress(EnumProgress state, float percent) {
                LOGGER.trace("Progress: {} {}", state, percent);
            }
        }); // Default
        builder.addJcefArgs("--gtk-version=3"); // Just an example
        builder.getCefSettings().windowless_rendering_enabled = true; // Default - select OSR mode

        // Set an app handler. Do not use CefApp.addAppHandler(...), it will break your code on MacOSX!
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
        });

        // Build a CefApp instance using the configuration above
        try {
            cefApp = builder.build();
        } catch (IOException | UnsupportedPlatformException | InterruptedException |
                 CefInitializationException e) {
            throw new RuntimeException(e);
        }
        cefClient = cefApp.createClient();
        cefClient.addRequestHandler(new CefRequestHandler() {
            @Override
            public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean user_gesture, boolean is_redirect) {
                System.out.printf("onBeforeBrowse " + request.getURL());
                return false;
            }

            @Override
            public boolean onOpenURLFromTab(CefBrowser browser, CefFrame frame, String target_url, boolean user_gesture) {
                return false;
            }

            @Override
            public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator, BoolRef disableDefaultHandling) {
                LOGGER.trace("getResourceRequestHandler {}", request.getURL());
                return null;
            }

            @Override
            public boolean getAuthCredentials(CefBrowser browser, String origin_url, boolean isProxy, String host, int port, String realm, String scheme, CefAuthCallback callback) {
                return false;
            }

            @Override
            public boolean onCertificateError(CefBrowser browser, CefLoadHandler.ErrorCode cert_error, String request_url, CefCallback callback) {
                return false;
            }

            @Override
            public void onRenderProcessTerminated(CefBrowser browser, TerminationStatus status, int error_code, String error_string) {

            }
        });
        cefClient.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                LOGGER.trace("onLoadStart {}", frame.getURL());
                notifyOnPageStarted(frame.getURL());
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                LOGGER.trace("onLoadEnd {}", frame.getURL());
                notifyOnPageFinished(frame.getURL(), "");
            }

            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                LOGGER.trace("onLoadError {}", frame.getURL());
                notifyOnPageError(frame.getURL(), errorText);
            }
        });
        cefBrowser = cefClient.createBrowser(null, true, true);
    }

    @Override
    public void performLoadUrl(@NotNull String url) {
        LOGGER.trace("performLoadUrl {}", url);
        cefBrowser.loadURL(url);
    }

    @Override
    public void performLoadData(@NotNull String data) {
        cefBrowser.loadURL(data);
    }

    @Override
    public void performReload() {
        cefBrowser.reload();
    }

    @Override
    public @Nullable String performEvaluate(@NotNull String javascript) {
        cefBrowser.executeJavaScript(javascript, cefBrowser.getURL(), 0);
        return "";
    }

    @Override
    public void onDestroy() {
        if (cefBrowser != null) {
            cefBrowser.stopLoad();
            cefBrowser.close(true);
        }

        if (cefClient != null) {
            cefClient.dispose();
        }

        if (cefApp != null) {
            cefApp.dispose();
        }
    }


    @Override
    public void dispatchMainThread(@NotNull Runnable task) {
        Platform.runLater(task);
    }

    @Override
    public boolean isMainThread() {
        return Platform.isFxApplicationThread();
    }
}
