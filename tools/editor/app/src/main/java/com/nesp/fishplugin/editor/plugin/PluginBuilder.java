package com.nesp.fishplugin.editor.plugin;

public class PluginBuilder {

    public static final int BUILD_STATUS_NONE = 0;
    public static final int BUILD_STATUS_BUILDING = 1;
    public static final int BUILD_STATUS_FAILED = 2;
    public static final int BUILD_STATUS_SUCCESS = 3;

    public static String getBuildStatusString(int status) {
        return switch (status) {
            case BUILD_STATUS_BUILDING -> "Building...";
            case BUILD_STATUS_FAILED -> "Build Failed";
            case BUILD_STATUS_SUCCESS -> "Build Success";
            default -> "";
        };
    }
}
