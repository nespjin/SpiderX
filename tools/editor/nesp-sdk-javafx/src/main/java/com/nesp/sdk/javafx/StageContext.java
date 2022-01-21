package com.nesp.sdk.javafx;

import com.google.common.util.concurrent.ListenableFuture;
import com.nesp.sdk.java.lang.AppObjRecycleWatcher;
import com.nesp.sdk.java.util.Log;
import com.nesp.sdk.javafx.concurrent.IThreadDispatcher;
import com.nesp.sdk.javafx.lifecycle.StageLifecycle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/29 12:15
 * Description:
 **/
public abstract class StageContext implements Context, StageLifecycle {

    private static final String TAG = "StageContext";
    private ContextWrapper mContextWrapper;
    private StageStyle mStageStyle;
    private volatile WeakReference<Stage> mStage;

    @NotNull
    public synchronized Stage getStage() {
        if (mStage == null || mStage.get() == null) {
            if (mStageStyle == null) {
                mStage = new WeakReference<>(new Stage());
            } else {
                mStage = new WeakReference<>(new Stage(mStageStyle));
            }
            onCreate(Objects.requireNonNull(mStage.get()));
        }
        return Objects.requireNonNull(mStage.get());
    }

    public StageContext() {
        getStage();
    }

    public StageContext(final StageStyle style) {
        this.mStageStyle = style;
        getStage();
    }

    @Override
    public void onCreate(@NotNull final Stage stage) {
        initialize(stage);
    }

    public void recreate() {
        mContextWrapper = null;
        onCreate(getStage());
    }

    protected void setTitle(final String title) {
        getStage().setTitle(title);
    }

    protected void setContent(final Object root) {
        setContent(((Parent) root));
    }

    protected void setContent(final Parent root) {
        getStage().setScene(new Scene(root));
    }

    protected void setContent(final String fxmlFile) {
        setContent(fxmlFile, this);
    }

    protected void setContent(final String fxmlFile, final Object controller) {
        try {
            final Node rootNode = Resource.tryLoadFxml(fxmlFile, this, controller);
            if (controller instanceof ControllerContext) {
                ((ControllerContext) controller).onCreate(rootNode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Stage stage) {
        mContextWrapper = new ContextWrapper();
        AppObjRecycleWatcher.getSingleton().observeIfStarted(this);
    }

    public void show() {
        final Stage stage = getStage();
        stage.setOnShowing(this::onShowing);
        stage.setOnShown(this::onShown);
        stage.setOnHiding(this::onHidding);
        stage.setOnHidden(this::onHidden);
        stage.setOnCloseRequest(this::onCloseRequest);
        stage.iconifiedProperty().addListener((observable, oldValue, newValue) -> onIconified(oldValue, newValue));
        stage.show();
    }

    private double mLastUnMaxX = -1;
    private double mLastUnMaxY = -1;
    private double mLastUnMaxWidth = -1;
    private double mLastUnMaxHeight = -1;

    public void setMaximized2(boolean maximized) {
        final Stage stage = getStage();

        if (maximized) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            mLastUnMaxX = stage.getX();
            mLastUnMaxY = stage.getY();
            mLastUnMaxWidth = stage.getWidth();
            mLastUnMaxHeight = stage.getHeight();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        } else {
            if (mLastUnMaxX > 0) {
                stage.setX(mLastUnMaxX);
            }

            if (mLastUnMaxY > 0) {
                stage.setY(mLastUnMaxY);
            }

            if (mLastUnMaxWidth > 0) {
                stage.setWidth(mLastUnMaxWidth);
            }

            if (mLastUnMaxHeight > 0) {
                stage.setHeight(mLastUnMaxHeight);
            }
        }
    }

    private void setMaximized(boolean maximized) {
        getStage().setMaximized(maximized);
    }

    public void showAndWait() {
        getStage().showAndWait();
    }

    public void initStyle(StageStyle style) {
        getStage().initStyle(style);
    }

    public boolean isResizable() {
        return getStage().isResizable();
    }

    public void setResizable(boolean resizable) {
        getStage().setResizable(resizable);
    }

    @Override
    public void onShowing(WindowEvent event) {
    }

    @Override
    public void onShown(WindowEvent event) {
    }

    @Override
    public void onHidding(WindowEvent event) {
    }

    @Override
    public void onHidden(WindowEvent event) {
        System.gc();
    }

    @Override
    public void onCloseRequest(WindowEvent event) {

    }

    @Override
    public void onIconified(final boolean oldValue, final boolean newValue) {
    }

    public void fireCloseEvent() {
        final Stage stage = getStage();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
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
}
