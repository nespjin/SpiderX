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
