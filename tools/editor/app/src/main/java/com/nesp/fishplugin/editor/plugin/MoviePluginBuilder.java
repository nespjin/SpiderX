package com.nesp.fishplugin.editor.plugin;

public class MoviePluginBuilder implements PluginBuilder {

    @Override
    public String[] getBuildTypes() {
        return new String[]{
                "Build",
                "Test Home Page",
                "Test Category Page",
                "Test Search Page",
                "Test Detail Page",
        };
    }
}
