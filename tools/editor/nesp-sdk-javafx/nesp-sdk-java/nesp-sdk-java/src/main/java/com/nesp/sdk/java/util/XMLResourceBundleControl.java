/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.java.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/12/2 上午10:51
 * Description:
 **/
public class XMLResourceBundleControl extends ResourceBundle.Control {

    private static final String XML = "xml";
    private static final List<String> SINGLETON_LIST = Collections.singletonList(XML);

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {

        if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
            throw new IllegalArgumentException("baseName, locale, format and loader cannot be null");
        }
        if (!format.equals(XML)) {
            throw new IllegalArgumentException("format must be xml");
        }

        final String bundleName = toBundleName(baseName, locale);
        final String resourceName = toResourceName(bundleName, format);
        final URL url = loader.getResource(resourceName);
        if (url == null) {
            return null;
        }

        final URLConnection urlconnection = url.openConnection();
        if (urlconnection == null) {
            return null;
        }

        if (reload) {
            urlconnection.setUseCaches(false);
        }

        try (final InputStream stream = urlconnection.getInputStream();
             final BufferedInputStream bis = new BufferedInputStream(stream);
        ) {
            return new XMLResourceBundle(bis);
        }
    }

    @Override
    public List<String> getFormats(String baseName) {
        return SINGLETON_LIST;
    }

}