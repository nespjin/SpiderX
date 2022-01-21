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
