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
