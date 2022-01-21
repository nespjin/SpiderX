package com.nesp.sdk.javafx;

import com.google.common.util.concurrent.ListenableFuture;
import com.nesp.sdk.java.lang.AppObjRecycleWatcher;
import com.nesp.sdk.javafx.concurrent.IThreadDispatcher;
import com.nesp.sdk.javafx.content.BroadcastReceiver;
import com.nesp.sdk.javafx.content.IntentFilter;
import com.nesp.sdk.javafx.content.LocalBroadcastManager;
import com.nesp.sdk.javafx.lifecycle.ControllerLifecycle;
import com.nesp.sdk.javafx.lifecycle.ControllerLifecycleObserver;
import com.nesp.sdk.javafx.lifecycle.ControllerLifecycleOwner;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/30 14:20
 * Description:
 **/
public abstract class ViewContext extends AnchorPane implements Context,
        ControllerLifecycle, ControllerLifecycleOwner {

    private static final String TAG = "ViewContext";

    private ContextWrapper mContextWrapper;
    private Node mRootNode;

    public ViewContext() {
        initialize();
        onCreate();
    }

    protected abstract void onCreate();

    protected void setContentView(final String fxmlFile) {
        setContentView(fxmlFile, this);
    }

    protected void setContentView(final String fxmlFile, final Object controller) {
        try {
            mRootNode = Resource.tryLoadFxml(fxmlFile, this, controller);
            if (controller instanceof ControllerContext) {
                ((ControllerContext) controller).onCreate(mRootNode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() {
        mContextWrapper = new ContextWrapper();
        AppObjRecycleWatcher.getSingleton().observeIfStarted(this);
    }

    public Node getRootNode() {
        return mRootNode;
    }

    @Override
    public Resource getResource() {
        return mContextWrapper.getResource();
    }

    @Override
    public ClassLoader getContextClassLoader() {
        return mContextWrapper.getContextClassLoader();
    }

    @Override
    public String getPackageName() {
        return mContextWrapper.getPackageName();
    }

    @Override
    public IThreadDispatcher newThreadDispatcher() {
        return mContextWrapper.newThreadDispatcher();
    }

    @Override
    public <R> ListenableFuture<R> runOnIOThread(final Runnable runnable) {
        return mContextWrapper.runOnIOThread(runnable);
    }

    @Override
    public <R> ListenableFuture<R> runOnDaemonIOThread(final Runnable runnable) {
        return mContextWrapper.runOnDaemonIOThread(runnable);
    }

    @Override
    public <R> ListenableFuture<R> runOnIOThread(final boolean isDaemon, final Runnable runnable) {
        return mContextWrapper.runOnIOThread(isDaemon, runnable);
    }

    @Override
    public void runOnUIThread(final Runnable runnable) {
        mContextWrapper.runOnUIThread(runnable);
    }

    @Override
    public void runOnUIThreadDelay(final long delay, final Runnable runnable) {
        mContextWrapper.runOnUIThreadDelay(delay, runnable);
    }

    @Override
    public final void onCreate(final Node root) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onAttachScene(final Scene scene, final Window window) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void observe(final Node node) {
        ControllerLifecycleObserver.observe(node, this);
    }

    @Override
    public void destroy() {
        onDestroy();
    }


    public void registerLocalBroadcastReceiver(final BroadcastReceiver receiver,
                                               final IntentFilter filter) {
        LocalBroadcastManager.getInstance().registerReceiver(receiver, filter);
    }

    public void unregisterLocalBroadcastReceiver(final BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance().unregisterReceiver(receiver);
    }
}
