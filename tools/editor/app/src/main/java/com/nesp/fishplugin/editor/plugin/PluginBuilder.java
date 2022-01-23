package com.nesp.fishplugin.editor.plugin;

public interface PluginBuilder {

    int BUILD_STATUS_NONE = 0;
    int BUILD_STATUS_BUILDING = 1;
    int BUILD_STATUS_FAILED = 2;
    int BUILD_STATUS_SUCCESS = 3;
    int BUILD_STATUS_STOP = 4;

    static String getBuildStatusString(int status) {
        return switch (status) {
            case BUILD_STATUS_BUILDING -> "Building...";
            case BUILD_STATUS_FAILED -> "Build Failed";
            case BUILD_STATUS_SUCCESS -> "Build Success";
            default -> "";
        };
    }

    String[] getBuildTypes();


}
