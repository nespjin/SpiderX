package com.nesp.fishplugin.editor.app;

import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

import java.util.Optional;

public abstract class AppBaseDialog<R> extends Dialog<R> {

    public AppBaseDialog() {
        dialogPaneProperty().addListener(new WeakChangeListener<>((observable, oldValue, newValue) ->
                Optional.ofNullable(newValue).map(Node::getScene).map(Scene::getWindow)
                .ifPresent(window -> StageUtil.initializeStage((Stage) window))));
    }
}
