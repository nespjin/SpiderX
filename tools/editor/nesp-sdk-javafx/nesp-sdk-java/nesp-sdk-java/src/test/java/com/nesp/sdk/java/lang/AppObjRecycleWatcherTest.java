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

import org.junit.jupiter.api.Test;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/18 下午10:44
 * Description:
 **/
class AppObjRecycleWatcherTest {

    @Test
    public void objectRecycle() {
        final TypeRecycleWatcher<Data> dataTypeRecycleWatcher = new TypeRecycleWatcher<>();
        dataTypeRecycleWatcher.start();

        while (true) {
            Data aaaaa = new Data(String.valueOf(System.currentTimeMillis()));
            dataTypeRecycleWatcher.observe(aaaaa);
            try {
                Thread.sleep(2000);
                aaaaa = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Data {
        String val;

        public Data(final String val) {
            this.val = val;
        }
    }


}