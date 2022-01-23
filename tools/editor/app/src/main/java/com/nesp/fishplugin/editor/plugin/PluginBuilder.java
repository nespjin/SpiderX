package com.nesp.fishplugin.editor.plugin;

import com.nesp.sdk.java.util.OnResultListener;

public interface PluginBuilder {

    int BUILD_STATUS_NONE = 0;
    int BUILD_STATUS_START = 1;
    int BUILD_STATUS_BUILDING = 2;
    int BUILD_STATUS_FAILED = 3;
    int BUILD_STATUS_SUCCESS = 4;
    int BUILD_STATUS_STOP = 5;

    static String getBuildStatusString(int status) {
        return switch (status) {
            case BUILD_STATUS_START -> "Start Build...";
            case BUILD_STATUS_BUILDING -> "Building...";
            case BUILD_STATUS_FAILED -> "Build Failed";
            case BUILD_STATUS_SUCCESS -> "Build Success";
            case BUILD_STATUS_STOP -> "Stop Build...";
            default -> "";
        };
    }

    String[] getBuildTypes();

    void build(int buildType, OnBuildProgressListener onBuildProgressListener);

    void stopBuild(OnResultListener<Boolean> onResultListener);

}
