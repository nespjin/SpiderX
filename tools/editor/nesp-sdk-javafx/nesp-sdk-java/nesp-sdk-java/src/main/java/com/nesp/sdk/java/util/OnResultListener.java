package com.nesp.sdk.java.util;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/7 18:31
 * Description:
 **/
@FunctionalInterface
public interface OnResultListener<T> {
    void onResult(T result);
}
