package com.nesp.sdk.javafx;

import com.nesp.sdk.javafx.content.BroadcastReceiver;
import com.nesp.sdk.javafx.content.IntentFilter;
import javafx.fxml.Initializable;
import javafx.scene.Node;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/4 0:08
 * Description:
 **/
public interface IController extends Initializable {

    Node getRootNode();

    void registerLocalBroadcastReceiver(BroadcastReceiver receiver, IntentFilter filter);

    void unregisterLocalBroadcastReceiver(BroadcastReceiver receiver);
}
