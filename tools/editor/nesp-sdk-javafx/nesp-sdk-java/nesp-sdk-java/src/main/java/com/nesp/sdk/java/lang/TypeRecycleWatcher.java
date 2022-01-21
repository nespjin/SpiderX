/*
 *
 *   Copyright (c) 2021  NESP Technology Corporation. All rights reserved.
 *
 *   This program is not free software; you can't redistribute it and/or modify it
 *   without the permit of team manager.
 *
 *   Unless required by applicable law or agreed to in writing.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.sdk.java.lang;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/18 下午8:38
 * Description:
 **/
public class TypeRecycleWatcher<T> {

    public static boolean sLogEnable = false;

    private static final String TAG = "TypeRecycleWatcher";

    private final ReferenceQueue<T> mTPhantomReferenceQueue;
    private final Map<PhantomReference<T>, WeakReference<T>> mMonitor;
    private Thread mWatcherThread;

    public TypeRecycleWatcher() {
        mTPhantomReferenceQueue = new ReferenceQueue<>();
        mMonitor = new HashMap<>();
    }

    public void observe(final T obj) {
        final PhantomReference<T> tPhantomReference = new PhantomReference<>(obj, mTPhantomReferenceQueue);
        mMonitor.put(tPhantomReference, new WeakReference<>(obj));
    }

    public void observeIfStarted(T obj) {
        if (isStarted()) {
            observe(obj);
        }
    }

    public boolean isStarted() {
        return mWatcherThread != null
                && mWatcherThread.isAlive()
                && !mWatcherThread.isInterrupted();
    }

    public void start() {
        if (mWatcherThread != null) {
            try {
                mWatcherThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mWatcherThread = new ObjectRecyclerWatchTask();
        mWatcherThread.start();
    }


    public void judgeMemory(T obj) {
        if (sLogEnable) System.out.println(TAG + ": 判断是否内存泄漏: ");
        for (WeakReference<T> weakReference : mMonitor.values()) {
            if (sLogEnable) System.out.println(TAG + ": 内存泄漏对象: " + weakReference.get());
        }
    }

    public Map<PhantomReference<T>, WeakReference<T>> getMonitor() {
        return mMonitor;
    }

    public ReferenceQueue<T> getTPhantomReferenceQueue() {
        return mTPhantomReferenceQueue;
    }


    private final class ObjectRecyclerWatchTask extends Thread {

        public ObjectRecyclerWatchTask() {
            setDaemon(true);
        }

        @Override
        @SuppressWarnings("all")
        public void run() {
            long lastPrint = 0;
            while (true) {
                final Reference<? extends T> poll = mTPhantomReferenceQueue.poll();
                if (poll != null) {
                    mMonitor.remove(poll);
                    if (sLogEnable) System.out.println(TAG + ": 引用被回收: " + poll);

                }

                if (System.currentTimeMillis() - lastPrint >= 3000) {
                    if (sLogEnable)
                        System.out.println(TAG + ": mMonitor.size(): " + mMonitor.size());
                    lastPrint = System.currentTimeMillis();
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
