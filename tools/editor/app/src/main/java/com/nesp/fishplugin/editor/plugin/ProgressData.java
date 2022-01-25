package com.nesp.fishplugin.editor.plugin;

record ProgressData(double progress, int lineType, String line) {

    ProgressData(double progress) {
        this(progress, 0, "");
    }

    ProgressData(int lineType, String line) {
        this(-2, lineType, (lineType == -1 ? "Build Failed: " : "") + line);
    }

}
