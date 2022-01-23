package com.nesp.fishplugin.editor.app;

import javafx.scene.image.Image;
import javafx.stage.Stage;

final class StageUtil {
    private StageUtil() {
        //no instance
    }

    static void initializeStage(final Stage stage) {
        if (stage == null) return;
        stage.getIcons().add(new Image("/drawable/ic_launcher_497_497.png"));
    }
}
