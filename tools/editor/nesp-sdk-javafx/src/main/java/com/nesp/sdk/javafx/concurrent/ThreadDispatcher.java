package com.nesp.sdk.javafx.concurrent;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/30 20:31
 * Description:
 **/
public final class ThreadDispatcher implements IThreadDispatcher {

    @SuppressWarnings("unused")
    private static final String TAG = "ThreadManager";

    private final ExecutorService mIOThreadPool =
            MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private final ExecutorService mIODaemonThreadPool =
            MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(new DaemonThreadFactory()));

    private static volatile ThreadDispatcher sInstance;

    public static ThreadDispatcher getInstance() {
        if (sInstance == null) {
            synchronized (ThreadDispatcher.class) {
                if (sInstance == null) {
                    sInstance = new ThreadDispatcher();
                }
            }
        }
        return sInstance;
    }

    private ThreadDispatcher() {
    }

    @Override
    public <R> ListenableFuture<R> runOnIOThread(final Runnable runnable) {
        return runOnIOThread(false, runnable);
    }

    @Override
    public <R> ListenableFuture<R> runOnDaemonIOThread(final Runnable runnable) {
        return runOnIOThread(true, runnable);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> ListenableFuture<R> runOnIOThread(boolean isDaemon, final Runnable runnable) {
        if (isDaemon) {
            return (ListenableFuture<R>) mIODaemonThreadPool.submit(runnable);
        } else {
            return (ListenableFuture<R>) mIOThreadPool.submit(runnable);
        }
    }

    @Override
    public void runOnUIThread(final Runnable runnable) {
        Platform.runLater(runnable);
    }

    @Override
    public void runOnUIThreadDelay(final long delay, final Runnable runnable) {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUIThread(runnable);
            }
        }, delay);
    }


}
