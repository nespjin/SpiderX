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

package com.nesp.sdk.javafx.control;

import com.nesp.sdk.java.text.TextUtil;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.ImageView;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/1 0:03
 * Description:
 **/
@SuppressWarnings("unused")
public final class TextInputControlUtil {

    private static final String TAG = "TextInputControlUtil";

    private TextInputControlUtil() {
    }

    /**
     * If text of inputControl is empty, clear will invisible, otherwise clear will visible
     *
     * @param inputControl input text controller
     * @param clear        image view for clear input text
     */
    public static void bindInputAndClear(final TextInputControl inputControl,
                                         final ImageView clear) {
        if (inputControl == null || clear == null) return;
        clear.setFitWidth(TextUtil.isNotEmpty(inputControl.getText()) ? 18 : 0.1);
        inputControl.textProperty().addListener((observable, oldValue, newValue) ->
                clear.setFitWidth(TextUtil.isNotEmpty(newValue) ? 18 : 0.1));
        clear.setOnMouseClicked(event -> inputControl.clear());
    }

}
