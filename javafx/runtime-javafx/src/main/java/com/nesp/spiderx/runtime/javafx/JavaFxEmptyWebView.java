package com.nesp.spiderx.runtime.javafx;

import com.nesp.spiderx.runtime.EmptyJniWebView;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

import javafx.application.Platform;

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
public class JavaFxEmptyWebView extends EmptyJniWebView {

    @Override
    public boolean isMainThread() {
        return Platform.isFxApplicationThread();
    }

    @Override
    public @NotNull ThreadFactory getMainThreadFactory() {
        return new DelegateThreadFactory(Platform::runLater);
    }

}
