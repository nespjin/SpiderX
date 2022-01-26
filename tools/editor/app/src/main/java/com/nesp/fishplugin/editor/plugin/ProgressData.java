package com.nesp.fishplugin.editor.plugin;

/**
 * @param progress in [-1,100]
 * @param lineType type for line, error for -1, normal for 0,warning for 1, success for 2
 * @param line line message
 */
record ProgressData(double progress, int lineType, String line) {

    ProgressData(double progress) {
        this(progress, 0, "");
    }

    ProgressData(int lineType, String line) {
        this(-3, lineType, (lineType == -1 ? "Build Failed: " : "") + line);
    }

}
