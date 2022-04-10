/*
 * Copyright (c) 2022.  NESP Technology.
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

