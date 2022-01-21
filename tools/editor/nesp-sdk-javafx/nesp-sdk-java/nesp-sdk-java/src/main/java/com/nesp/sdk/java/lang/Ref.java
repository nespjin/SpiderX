package com.nesp.sdk.java.lang;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2021/8/17 15:37
 **/
public class Ref<T> {

    private T value;

    public Ref(final T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Ref<T> setValue(final T value) {
        this.value = value;
        return this;
    }
}
