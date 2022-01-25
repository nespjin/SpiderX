package com.nesp.fishplugin.editor.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class AppThreadManager {
    private AppThreadManager() {
        //no instance
    }

    private static final class SInstanceHolder {
        private static final AppThreadManager sInstance = new AppThreadManager();
    }

    public static AppThreadManager getInstance() {
        return SInstanceHolder.sInstance;
    }

    //    private final ExecutorService threadPool = Executors.newWorkStealingPool();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10, new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });

    public ExecutorService getThreadPool() {
        return threadPool;
    }


}

