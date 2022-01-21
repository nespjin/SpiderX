package com.nesp.sdk.java.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/9/3 16:31
 **/
public class ListUtil {
    private static final String TAG = "ListUtil";

    public static <T> List<T> of(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }
}
