package com.nesp.fishplugin.editor.app;

import com.nesp.sdk.javafx.BaseStage;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public abstract class AppBaseStage extends BaseStage {

    @Override
    public void onCreate(@NotNull Stage stage) {
        super.onCreate(stage);
        StageUtil.initializeStage(stage);
    }
}
