/*
 * Copyright  2019.  靳兆鲁（1756404649@qq.com）
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
 *
 */

package com.nesp.sdk.javafx.utils;


import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class ControllerUtils {

    /**
     * 宽度绑定宽度
     *
     * @param subWidget    子控件
     * @param parentWidget 父控件
     */
    public static void bindWidth(Region subWidget, Region parentWidget) {
        subWidget.prefWidthProperty().bind(parentWidget.widthProperty());
    }

    public static void bindWidth(Region subWidget, Stage parentWidget) {
        subWidget.prefWidthProperty().bind(parentWidget.widthProperty());
    }

    /**
     * 高度绑定高度
     *
     * @param subWidget    子控件
     * @param parentWidget 父控件
     */
    public static void bindHeight(Region subWidget, Region parentWidget) {
        subWidget.prefHeightProperty().bind(parentWidget.heightProperty());
    }

    public static void bindHeight(Region subWidget, Stage parentWidget) {
        subWidget.prefHeightProperty().bind(parentWidget.heightProperty());
    }

    public static void bindHeightAndWidth(Region subWidget, Region parentWidget) {
        bindHeight(subWidget, parentWidget);
        bindWidth(subWidget, parentWidget);
    }

    public static void bindHeightAndWidth(Region subWidget, Stage parentWidget) {
        bindHeight(subWidget, parentWidget);
        bindWidth(subWidget, parentWidget);
    }
}
