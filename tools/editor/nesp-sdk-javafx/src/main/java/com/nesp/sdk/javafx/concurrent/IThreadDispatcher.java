package com.nesp.sdk.javafx.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/8 下午1:50
 * Description:
 **/
public interface IThreadDispatcher {

    <R> ListenableFuture<R> runOnIOThread(Runnable runnable);

    <R> ListenableFuture<R> runOnDaemonIOThread(Runnable runnable);

    <R> ListenableFuture<R> runOnIOThread(boolean isDaemon, Runnable runnable);

    void runOnUIThread(Runnable runnable);

    void runOnUIThreadDelay(long delay, Runnable runnable);
}
