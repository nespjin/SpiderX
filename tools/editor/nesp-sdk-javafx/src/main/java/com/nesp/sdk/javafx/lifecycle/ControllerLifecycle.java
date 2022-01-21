package com.nesp.sdk.javafx.lifecycle;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/3 20:59
 * Description:
 * lifecycle as below:
 * {@link Initializable#initialize(URL, ResourceBundle)}
 * {@link #onCreate(Node)}
 * {@link #onAttachScene(Scene, Window)}
 **/
public interface ControllerLifecycle extends Lifecycle {

    /**
     * Call when root node set, must call super on child class.
     * this method will not be called if special controller in FXML with <fx:controller>
     *
     * @param root root node of this controller.
     */
    void onCreate(Node root);

    /**
     * Call when controller attach scene.
     * this method will not be called if special controller in FXML with <fx:controller> or
     * not call {@link ControllerLifecycleOwner#observe(Node)}
     *
     * @param scene  scene hold current controller.
     * @param window window(Stage) hold current scene.
     */
    void onAttachScene(final Scene scene, final Window window);

    /**
     * Call when destroy called.
     */
    void onDestroy();

}
