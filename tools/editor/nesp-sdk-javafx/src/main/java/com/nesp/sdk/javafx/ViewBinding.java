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

package com.nesp.sdk.javafx;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/26 上午9:43
 * Description:
 **/
public class ViewBinding {

    private static final String TAG = "ViewBinding";

    public static <T extends ViewBinding> T inflate(final String fxmlFile,
                                                    final ResourceBundle resourceBundle,
                                                    final T viewBinding) {
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    Objects.requireNonNull(contextClassLoader.getResource(fxmlFile)), resourceBundle);
            if (viewBinding != null) {
                fxmlLoader.setController(viewBinding);
            }
            fxmlLoader.load();
            return viewBinding;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
