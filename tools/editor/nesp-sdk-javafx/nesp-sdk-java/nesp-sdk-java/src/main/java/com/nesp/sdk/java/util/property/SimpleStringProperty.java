package com.nesp.sdk.java.util.property;

/**
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/4 下午1:42
 * Description:
 **/
public class SimpleStringProperty extends BaseProperty<String> {

    public SimpleStringProperty() {
        this("");
    }

    public SimpleStringProperty(final String value) {
        super(value);
    }
}
