package com.nesp.sdk.javafx.utils;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/10 上午8:43
 * Description:
 **/
public final class WindowUtil {

    private WindowUtil() {/* no instance */}

    public static void fireCloseEvent(final Window window) {
        if (window == null) return;
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public static void fireHoseWindowCloseEvent(final Node node) {
        if (node == null) return;
        fireCloseEvent(node.getScene().getWindow());
    }

    public static void setWindowRootNodeStyle(final Node rootNode) {
        if (rootNode == null) return;
        final DropShadow dropshadow = new DropShadow();// 阴影向外
        dropshadow.setRadius(10);// 颜色蔓延的距离
        dropshadow.setOffsetX(0);// 水平方向，0则向左右两侧，正则向右，负则向左
        dropshadow.setOffsetY(0);// 垂直方向，0则向上下两侧，正则向下，负则向上
        dropshadow.setSpread(0.1);// 颜色变淡的程度
        dropshadow.setColor(Color.BLACK);// 设置颜色
        rootNode.setEffect(dropshadow);
    }

}
