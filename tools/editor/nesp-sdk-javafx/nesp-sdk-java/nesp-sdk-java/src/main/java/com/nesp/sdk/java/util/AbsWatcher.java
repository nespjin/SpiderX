package com.nesp.sdk.java.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/27 下午3:30
 * Description:
 **/
public abstract class AbsWatcher implements Watcher {

    private static final long WATCH_KEEP_LIVE_TIME_MILLISECONDS = 30 * 1000;

    private Timer mTimer;
    private long mWatchDelay = 0;
    private long mWatchPeriod = 5000;
    private long mWatchLastRunTimeMilliseconds = 0;

    @Override
    public void reWatchIfNotRunning() {
        if (!isRunning()) watch();
    }

    @Override
    public void watch() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mWatchLastRunTimeMilliseconds = System.currentTimeMillis();
                if (needRest()) reset();
            }
        }, mWatchDelay, mWatchPeriod);
    }

    @Override
    public void setTimer(final Timer timer) {
        mTimer = timer;
    }

    @Override
    public void setWatchDelay(final long watchDelay) {
        mWatchDelay = watchDelay;
    }

    @Override
    public void setWatchPeriod(final long watchPeriod) {
        mWatchPeriod = watchPeriod;
    }

    @Override
    public abstract void reset();

    @Override
    public abstract boolean needRest();

    @Override
    public boolean isRunning() {
        return System.currentTimeMillis() - mWatchLastRunTimeMilliseconds - mWatchDelay - mWatchPeriod
                < WATCH_KEEP_LIVE_TIME_MILLISECONDS;
    }
}
