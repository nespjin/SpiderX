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

package com.nesp.fishplugin.editor.app;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;
import java.util.function.Consumer;

public class AppAlert extends Alert {

    public AppAlert(AlertType alertType) {
        super(alertType);
        initialize();
    }

    public AppAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        initialize();
    }

    private void initialize() {
        Optional.ofNullable(getDialogPane()).map(Node::getScene).map(Scene::getWindow)
                .ifPresent(new Consumer<Window>() {
                    @Override
                    public void accept(Window window) {
                        StageUtil.initializeStage((Stage) window);
                    }
                });
    }

}
