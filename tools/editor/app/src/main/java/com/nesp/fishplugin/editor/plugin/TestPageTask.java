package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.editor.project.Project;

import java.util.Locale;

public class TestPageTask implements PluginBuildTask {

    private final String pageId;
    private final int type;

    /**
     * @param pageId id of page
     * @param type   0 dsl, 1 js
     */
    public TestPageTask(String pageId, int type) {
        this.pageId = pageId;
        this.type = type;
    }

    @Override
    public String name() {
        String typeString = type == 0 ? "DSL" : "Js";
        String pageId2 = pageId.substring(0, 1).toUpperCase(Locale.ROOT) + pageId.substring(1);
        return "Test " + pageId2 + " Page" + ": " + typeString;
    }

    @Override
    public Result run(Project workingProject, Object... parameters) throws Exception {
        return null;
    }

    @Override
    public PluginBuildTask[] dependencies() {
        return new PluginBuildTask[0];
    }

}
