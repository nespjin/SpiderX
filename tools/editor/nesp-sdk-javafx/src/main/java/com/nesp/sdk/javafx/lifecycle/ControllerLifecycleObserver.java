package com.nesp.sdk.javafx.lifecycle;

import com.nesp.sdk.javafx.concurrent.ThreadDispatcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/30 20:29
 * Description:
 **/
@SuppressWarnings("all")
public final class ControllerLifecycleObserver implements LifecycleObserver {

    private static final String TAG = "ControllerUtil";

    public static void observe(final Node node,
                               final ControllerLifecycle lifecycle) {
        Objects.requireNonNull(node);
        node.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(final ObservableValue<? extends Scene> observable,
                                final Scene oldValue, final Scene newValue) {
                if (lifecycle != null && node != null && newValue != null) {
                    ThreadDispatcher.getInstance().runOnUIThread(() ->
                            lifecycle.onAttachScene(node.getScene(), newValue.getWindow()));
                }
                node.sceneProperty().removeListener(this);
            }
        });
        /*ThreadDispatcher.getInstance().runOnIOThread(() -> {
            while (node.getScene() == null) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (owner != null) {
                ThreadDispatcher.getInstance().runOnUIThread(() ->
                        owner.onAttachScene(node.getScene(),
                                node.getScene().getWindow()));
            }
        });*/
    }

    public static void observe(final Stage stage,
                               final ControllerLifecycle lifecycle) {
        ThreadDispatcher.getInstance().runOnIOThread(() -> {
            while (stage.getScene() == null) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (lifecycle != null) {
                ThreadDispatcher.getInstance().runOnUIThread(() ->
                        lifecycle.onAttachScene(stage.getScene(),
                                stage.getScene().getWindow()));
            }
        });
    }


}
