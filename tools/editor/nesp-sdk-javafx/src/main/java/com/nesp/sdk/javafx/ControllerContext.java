package com.nesp.sdk.javafx;

import com.nesp.sdk.java.annotation.NonNull;
import com.nesp.sdk.java.lang.AppObjRecycleWatcher;
import com.nesp.sdk.javafx.content.BroadcastReceiver;
import com.nesp.sdk.javafx.content.IntentFilter;
import com.nesp.sdk.javafx.content.LocalBroadcastManager;
import com.nesp.sdk.javafx.lifecycle.ControllerLifecycle;
import com.nesp.sdk.javafx.lifecycle.ControllerLifecycleObserver;
import com.nesp.sdk.javafx.lifecycle.ControllerLifecycleOwner;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/8/8 22:28
 * Project: JavaFx SDK
 * Description:
 * <p>
 * Controller for FXML file,
 **/
public abstract class ControllerContext extends ContextWrapper implements Initializable,
        ControllerLifecycle, ControllerLifecycleOwner {

    @SuppressWarnings("unused")
    private static final String TAG = "ControllerContext";


    /**
     * root node of current FXML file.
     */
    private Node mRootNode;

    /**
     * Call when FXML parsed completed. All controls will be set in this method.
     *
     * @param location  location of current FXML file.
     * @param resources resource of current FXML file.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        AppObjRecycleWatcher.getSingleton().observeIfStarted(this);
    }


    @Override
    public void onCreate(final Node rootNode) {
        Objects.requireNonNull(rootNode);
        mRootNode = rootNode;
        if (getRootNode() != null) {
            observe(getRootNode());
        }
    }

    @Override
    public void onAttachScene(final Scene scene, final Window window) {

    }

    @Override
    public void observe(@NonNull Node node) {
        ControllerLifecycleObserver.observe(node, this);
    }

    public Node getRootNode() {
        return mRootNode;
    }

    @Override
    public void destroy() {
        onDestroy();
    }

    @Override
    public void onDestroy() {

    }

    public void registerLocalBroadcastReceiver(final BroadcastReceiver receiver,
                                               final IntentFilter filter) {
        LocalBroadcastManager.getInstance().registerReceiver(receiver, filter);
    }

    public void unregisterLocalBroadcastReceiver(final BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance().unregisterReceiver(receiver);
    }

}
