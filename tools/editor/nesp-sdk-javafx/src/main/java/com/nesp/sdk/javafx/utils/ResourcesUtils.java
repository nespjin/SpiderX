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

package com.nesp.sdk.javafx.utils;

import javafx.scene.image.Image;
import javafx.stage.Stage;

@Deprecated
public class ResourcesUtils {

    public static Image getImageByStream(Object controller, String path) {
        return new Image(controller.getClass().getResourceAsStream(path));
    }

    public static Image getImageByStream(Stage stage, String path) {
        return new Image(stage.getClass().getResourceAsStream(path));
    }
}
