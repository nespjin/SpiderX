package com.nesp.sdk.javafx.concurrent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/8 下午2:07
 * Description:
 **/
public abstract class WorkRunnable<P, W> implements Runnable {

    private static final Object STEP_LOCK = new Object();
    private final IThreadDispatcher mThreadDispatcher;
    private final Map<String, Object> mParamsMap = new WeakHashMap<>();

    public WorkRunnable() {
        this(ThreadDispatcher.getInstance());
    }

    public WorkRunnable(final IThreadDispatcher threadDispatcher) {
        mThreadDispatcher = threadDispatcher;
    }

    public P runOnPreviousWork() {
        return null;
    }

    public abstract W runOnWork(P previousWorkResult);

    private void putPreviousWorkResult(P previousWorkResult) {
        getParams().put("previousWorkResult", previousWorkResult);
    }

    @SuppressWarnings("unchecked")
    private P getPreviousWorkResult() {
        return (P) getParams().get("previousWorkResult");
    }

    private void putWorkResult(W workResult) {
        getParams().put("workResult", workResult);
    }

    @SuppressWarnings("unchecked")
    private W getWorkResult() {
        return (W) getParams().get("workResult");
    }

    private Map<String, Object> getParams() {
        return mParamsMap;
    }

    @Override
    public void run() {
        mThreadDispatcher.runOnUIThread(() -> {
            putPreviousWorkResult(runOnPreviousWork());
            synchronized (STEP_LOCK) {
                STEP_LOCK.notifyAll();
            }
        });

        synchronized (STEP_LOCK) {
            try {
                STEP_LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            putWorkResult(runOnWork(getPreviousWorkResult()));
            mThreadDispatcher.runOnUIThread(() -> {
                runOnAfterWork(getWorkResult());
                getParams().clear();
            });
        }
    }

    public void runOnAfterWork(W workResult) {
    }

}
