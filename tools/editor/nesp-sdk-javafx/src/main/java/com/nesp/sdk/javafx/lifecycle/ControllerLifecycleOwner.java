package com.nesp.sdk.javafx.lifecycle;

import com.nesp.sdk.java.annotation.NonNull;
import javafx.scene.Node;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/10/30 20:45
 * Description:
 **/
public interface ControllerLifecycleOwner extends LifecycleOwner {

    /**
     * Observe controller lifecycle with node.
     *
     * @param node node to be observed
     */
    void observe(@NonNull Node node);

    void destroy();

}
