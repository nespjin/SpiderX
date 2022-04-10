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

package com.nesp.sdk.javafx;


import com.google.common.util.concurrent.ListenableFuture;
import com.nesp.sdk.javafx.concurrent.IThreadDispatcher;
import com.nesp.sdk.javafx.concurrent.ThreadDispatcher;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/8 22:13
 * Project: JavaFx SDK
 * Description:
 **/
public class ContextWrapper implements Context {

    private final Resource mResource;
    private final ClassLoader mContextClassLoader;
    private final IThreadDispatcher mThreadDispatcher;

    public ContextWrapper() {
        mContextClassLoader = Thread.currentThread().getContextClassLoader();
        mResource = new Resource(this);
        mThreadDispatcher = newThreadDispatcher();
    }

    @Override
    public IThreadDispatcher newThreadDispatcher() {
        return ThreadDispatcher.getInstance();
    }

    @Override
    public Resource getResource() {
        return mResource;
    }

    @Override
    public ClassLoader getContextClassLoader() {
        return mContextClassLoader;
    }

    @Override
    public String getPackageName() {
        throw new UnsupportedOperationException("This method is not implements yet!!");
    }

    @Override
    public <R> ListenableFuture<R> runOnIOThread(final Runnable runnable) {
        return mThreadDispatcher.runOnIOThread(runnable);
    }

    @Override
    public <R> ListenableFuture<R> runOnDaemonIOThread(final Runnable runnable) {
        return mThreadDispatcher.runOnDaemonIOThread(runnable);
    }

    @Override
    public <R> ListenableFuture<R> runOnIOThread(final boolean isDaemon, final Runnable runnable) {
        return mThreadDispatcher.runOnIOThread(isDaemon, runnable);
    }

    @Override
    public void runOnUIThread(final Runnable runnable) {
        mThreadDispatcher.runOnUIThread(runnable);
    }

    @Override
    public void runOnUIThreadDelay(final long delay, final Runnable runnable) {
        mThreadDispatcher.runOnUIThreadDelay(delay, runnable);
    }
}
