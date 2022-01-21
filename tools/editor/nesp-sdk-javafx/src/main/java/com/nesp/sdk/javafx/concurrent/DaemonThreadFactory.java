package com.nesp.sdk.javafx.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/31 23:38
 * Description:
 **/
public final class DaemonThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(@NotNull final Runnable r) {
        final Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    }
}
