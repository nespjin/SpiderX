package com.nesp.sdk.java.util.property;

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/4 下午1:17
 * Description:
 **/
public interface Property {

    interface OnPropertyChangedListener<T> {
        void onChanged(Property property, T oldValue, T newValue);
    }

    void invalidate();

}
