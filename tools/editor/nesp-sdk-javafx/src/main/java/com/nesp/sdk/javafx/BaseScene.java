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

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Paint;

import java.io.IOException;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/29 11:51
 * Description:
 **/
public abstract class BaseScene extends SceneContext {

    private static final String TAG = "BaseScene";

    public BaseScene(final String fxmlFileName) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName));
    }

    public BaseScene(final String fxmlFileName, final Object controller) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName, (Object) null, controller));
    }

    public BaseScene(final String fxmlFileName, final Object root, final Object controller) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName, root, controller));
    }

    public BaseScene(final Parent root) {
        super(root);
    }

    public BaseScene(final String fxmlFileName, final double width, final double height) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName), width, height);
    }

    public BaseScene(final Parent root, final double width, final double height) {
        super(root, width, height);
    }

    public BaseScene(final String fxmlFileName, final Paint fill) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName), fill);
    }

    public BaseScene(final Parent root, final Paint fill) {
        super(root, fill);
    }

    public BaseScene(final String fxmlFileName, final double width, final double height, final Paint fill) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName), width, height, fill);
    }

    public BaseScene(final Parent root, final double width, final double height, final Paint fill) {
        super(root, width, height, fill);
    }

    public BaseScene(final String fxmlFileName, final double width, final double height, final boolean depthBuffer) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName), width, height, depthBuffer);
    }

    public BaseScene(final Parent root, final double width, final double height, final boolean depthBuffer) {
        super(root, width, height, depthBuffer);
    }

    public BaseScene(final String fxmlFileName, final double width, final double height, final boolean depthBuffer, final SceneAntialiasing antiAliasing) throws IOException {
        super(Resource.tryLoadFxml(fxmlFileName), width, height, depthBuffer, antiAliasing);
    }

    public BaseScene(final Parent root, final double width, final double height, final boolean depthBuffer, final SceneAntialiasing antiAliasing) {
        super(root, width, height, depthBuffer, antiAliasing);
    }

    public Node $(String id) {
        return lookup("#" + id);
    }

    public void onBindController(final Object controller) {

    }
}
