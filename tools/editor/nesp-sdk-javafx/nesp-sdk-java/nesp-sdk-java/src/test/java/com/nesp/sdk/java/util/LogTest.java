package com.nesp.sdk.java.util;

import org.junit.jupiter.api.Test;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/3 23:07
 * Description:
 **/
class LogTest {

    @Test
    void i() {
        Log.i("LogTest", "This is info");
    }

    @Test
    void w() {
        Log.w("LogTest", "This is warn");
    }

    @Test
    void e() {
        Log.e("LogTest", "This is error");
    }
}