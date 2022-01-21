package com.nesp.sdk.java.util;

import java.util.Timer;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/27 下午3:26
 * Description:
 **/
public interface Watcher {

    void reWatchIfNotRunning();

    void watch();

    void setTimer(Timer timer);

    void setWatchDelay(long watchDelay);

    void setWatchPeriod(long watchPeriod);

    void reset();

    boolean needRest();

    boolean isRunning();
}
