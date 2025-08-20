package com.nesp.spiderx.runtime.javafx;

import com.nesp.spiderx.runtime.EmptyJniWebView;

import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
public class JavaFxEmptyWebView extends EmptyJniWebView {

    @Override
    public void dispatchMainThread(@NotNull Runnable task) {
        Platform.runLater(task);
    }

    @Override
    public boolean isMainThread() {
        return Platform.isFxApplicationThread();
    }
}
