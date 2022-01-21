package com.nesp.sdk.java.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/12/2 上午10:50
 * Description:
 **/
public class XMLResourceBundle extends ResourceBundle {

    private final Properties mProperties;

    public XMLResourceBundle(InputStream stream) throws IOException {
        mProperties = new Properties();
        mProperties.loadFromXML(stream);
    }

    @Override
    protected Object handleGetObject(String key) {
        return mProperties.getProperty(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        Set<String> handleKeys = mProperties.stringPropertyNames();
        return Collections.enumeration(handleKeys);
    }

}
