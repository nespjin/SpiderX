package com.nesp.fishplugin.editor.plugin;

public interface OnBuildProgressListener {

    /**
     * @param progress in [-1,100]
     * @param lineType type for line, error for -1, normal for 0,warning for 1, success for 2
     * @param line line message
     */
    void onProgress(double progress, int lineType, String line);
}
