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

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/4 下午9:07
 * Description:
 **/
public class WindowHelper {
    private static boolean isRight;// 是否处于右边界调整窗口状态
    private static boolean isBottomRight;// 是否处于右下角调整窗口状态
    private static boolean isBottom;// 是否处于下边界调整窗口状态
    private static final int RESIZE_WIDTH = 2;// 判定是否为调整窗口状态的范围与边界距离
    private double xOffset = 0;
    private double yOffset = 0;


    public void bindDrag(Node node) {
//        if (node.getParent() == null) {
//            DropShadow dropshadow = new DropShadow();// 阴影向外
//            dropshadow.setRadius(10);// 颜色蔓延的距离
//            dropshadow.setOffsetX(0);// 水平方向，0则向左右两侧，正则向右，负则向左
//            dropshadow.setOffsetY(0);// 垂直方向，0则向上下两侧，正则向下，负则向上
//            dropshadow.setSpread(0.1);// 颜色变淡的程度
//            dropshadow.setColor(Color.BLACK);// 设置颜色
//            node.setEffect(dropshadow);
//        }
        node.setOnMouseMoved(event -> {
            event.consume();
            double x = event.getSceneX();
            double y = event.getSceneY();

            final Window window = node.getScene().getWindow();
            double width = window.getWidth();
            double height = window.getHeight();
            Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型

            // 先将所有调整窗口状态重置
            isRight = isBottomRight = isBottom = false;
            if (y >= height - RESIZE_WIDTH) {
                if (x <= RESIZE_WIDTH) {// 左下角调整窗口状态

                } else if (x >= width - RESIZE_WIDTH) {// 右下角调整窗口状态
                    isBottomRight = true;
                    cursorType = ((Stage) window).isResizable() ? Cursor.SE_RESIZE : Cursor.DEFAULT;
                } else {// 下边界调整窗口状态
                    isBottom = true;
                    cursorType = ((Stage) window).isResizable() ? Cursor.S_RESIZE : Cursor.DEFAULT;
                }
            } else if (x >= width - RESIZE_WIDTH) {// 右边界调整窗口状态
                isRight = true;
                cursorType = ((Stage) window).isResizable() ? Cursor.E_RESIZE : Cursor.DEFAULT;
            }

            // 最后改变鼠标光标
            node.setCursor(cursorType);
        });

        node.setOnMouseDragged(event -> {
            double x = event.getSceneX();
            double y = event.getSceneY();

            final Stage stage = (Stage) node.getScene().getWindow();
            // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
            double nextX = stage.getX();
            double nextY = stage.getY();
            double nextWidth = stage.getWidth();
            double nextHeight = stage.getHeight();

            if (isRight || isBottomRight) {// 所有右边调整窗口状态
                nextWidth = x;
            }
            if (isBottomRight || isBottom) {// 所有下边调整窗口状态
                nextHeight = y;
            }
            if (nextWidth <= stage.getMinWidth()) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                nextWidth = stage.getMinWidth();
            }
            if (nextHeight <= stage.getMinHeight()) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
                nextHeight = stage.getMinHeight();
            }

            // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
            stage.setX(nextX);
            stage.setY(nextY);

            final Window window = node.getScene().getWindow();
            if (((Stage) window).isResizable()) {
                stage.setWidth(nextWidth);
                stage.setHeight(nextHeight);
            }
            if (!isBottom && !isBottomRight && !isRight) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        node.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
    }


    private double mLastUnMaxX = -1;
    private double mLastUnMaxY = -1;
    private double mLastUnMaxWidth = -1;
    private double mLastUnMaxHeight = -1;

    public void setMaximized2(final Stage stage, boolean maximized) {
        if (maximized) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            mLastUnMaxX = stage.getX();
            mLastUnMaxY = stage.getY();
            mLastUnMaxWidth = stage.getWidth();
            mLastUnMaxHeight = stage.getHeight();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        } else {
            if (mLastUnMaxX > 0) {
                stage.setX(mLastUnMaxX);
            }

            if (mLastUnMaxY > 0) {
                stage.setY(mLastUnMaxY);
            }

            if (mLastUnMaxWidth > 0) {
                stage.setWidth(mLastUnMaxWidth);
            }

            if (mLastUnMaxHeight > 0) {
                stage.setHeight(mLastUnMaxHeight);
            }
        }
    }

}
