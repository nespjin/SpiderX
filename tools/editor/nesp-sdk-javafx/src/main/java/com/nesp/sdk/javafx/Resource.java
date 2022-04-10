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

import com.nesp.sdk.java.util.XMLResourceBundleControl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/8 17:21
 * Project: JavaFx SDK
 * Description:
 **/
public class Resource {

    private final ResourceBundle mStringResourceBundle;
    private final AssetsManager mAssetsManager;
    private final Context mContext;

    public Resource(Context context) {
        this.mContext = context;
        mStringResourceBundle = newStringResourceBundle();
        mAssetsManager = new AssetsManager(context);
    }

    public String getString(String key) {
        return mStringResourceBundle.getString(key);
    }

    public String getString(String key, Object... params) {
        return MessageFormat.format(mStringResourceBundle.getString(key), params);
    }

    public String getStyle(@NotNull String styleFileName) {
        if (!styleFileName.endsWith(".css")) {
            styleFileName = styleFileName.concat(".css");
        }
        return Objects.requireNonNull(mContext.getContextClassLoader().getResource("styles/".concat(styleFileName))).toExternalForm();
    }

    public Parent loadFxml(@NotNull String fxmlFileName) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getLayoutURL(fxmlFileName)));
    }

    public FXMLLoader fxmlLoader(@NotNull String fxmlFileName) {
        return new FXMLLoader(getLayoutURL(fxmlFileName));
    }

    public URL getLayoutURL(@NotNull String fxmlFileName) {
        if (!fxmlFileName.endsWith(".fxml")) {
            fxmlFileName = fxmlFileName.concat(".fxml");
        }
        return mContext.getContextClassLoader().getResource("layout/".concat(fxmlFileName));
    }

    public static <T> T tryLoadFxml(@NotNull String fxmlFileName) throws IOException {
        return tryLoadFxml(fxmlFileName, newStringResourceBundle());
    }

    public static ResourceBundle newStringResourceBundle() {
        try {
            return ResourceBundle.getBundle("strings/strings");
        } catch (MissingResourceException e) {
            return ResourceBundle.getBundle("strings.strings", new XMLResourceBundleControl());
        }
    }

    public static <T> T tryLoadFxml(@NotNull String fxmlFileName,
                                    @NotNull ResourceBundle resourceBundle) throws IOException {
        return tryLoadFxml(fxmlFileName, resourceBundle, null, null);
    }

    public static <T> T tryLoadFxmlWithController(@NotNull String fxmlFileName,
                                                  @NotNull ResourceBundle resourceBundle,
                                                  @Nullable Object controller) throws IOException {
        return tryLoadFxml(fxmlFileName, resourceBundle, null, controller);
    }

    public static <T> T tryLoadFxml(@NotNull String fxmlFileName,
                                    @NotNull ResourceBundle resourceBundle,
                                    @Nullable Object root) throws IOException {
        return tryLoadFxml(fxmlFileName, resourceBundle, root, null);
    }

    public static <T> T tryLoadFxmlWithController(@NotNull String fxmlFileName,
                                                  @Nullable Object controller) throws IOException {
        return tryLoadFxml(fxmlFileName, newStringResourceBundle(), null, controller);
    }

    public static <T> T tryLoadFxml(@NotNull String fxmlFileName,
                                    @Nullable Object root) throws IOException {
        return tryLoadFxml(fxmlFileName, newStringResourceBundle(), root, null);
    }

    public static <T> T tryLoadFxml(@NotNull String fxmlFileName,
                                    @Nullable Object root,
                                    @Nullable Object controller) throws IOException {
        return tryLoadFxml(fxmlFileName, newStringResourceBundle(), root, controller);
    }

    public static <T> T tryLoadFxml(@NotNull String fxmlFileName,
                                    @NotNull ResourceBundle resourceBundle,
                                    @Nullable Object root,
                                    @Nullable Object controller) throws IOException {
        final FXMLLoader fxmlLoader = newFXMLLoader(fxmlFileName, resourceBundle);
        if (root != null) {
            fxmlLoader.setRoot(root);
        }
        if (controller != null) {
            fxmlLoader.setController(controller);
        }
        return fxmlLoader.load();
    }

    public <T> T loadFxml(@NotNull URL location) throws IOException {
        return FXMLLoader.load(location);
    }

    public static FXMLLoader newFXMLLoader(@NotNull String fxmlFileName,
                                           @NotNull ResourceBundle resourceBundle) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return new FXMLLoader(Objects.requireNonNull(
                contextClassLoader.getResource(fxmlFilePath(fxmlFileName))),
                resourceBundle);
    }

    public static String fxmlFilePath(@NotNull String fxmlFileName) {
        if (!fxmlFileName.endsWith(".fxml")) {
            fxmlFileName = fxmlFileName.concat(".fxml");
        }
        return "layout/".concat(fxmlFileName);
    }

    public AssetsManager getAssets() {
        return mAssetsManager;
    }

}
