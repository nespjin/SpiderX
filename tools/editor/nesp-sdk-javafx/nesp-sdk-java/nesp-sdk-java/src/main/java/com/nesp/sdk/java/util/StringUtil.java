package com.nesp.sdk.java.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/9/3 16:24
 **/
public final class StringUtil {
    private static final String TAG = "StringUtil";

    private StringUtil() {
    }

    public static String join(String separator, String... strings) {
        return join(separator, ListUtil.of(strings));
    }

    public static String join(String separator, Collection<String> strings) {
        return join(separator, strings.iterator());
    }

    public static String join(String separator, Iterator<String> stringIterator) {
        StringBuilder rStringBuilder = new StringBuilder();
        while (stringIterator.hasNext()) {
            if (rStringBuilder.length() > 0) {
                rStringBuilder.append(",");
            }
            rStringBuilder.append(stringIterator.next());
        }
        return rStringBuilder.toString();
    }
}
