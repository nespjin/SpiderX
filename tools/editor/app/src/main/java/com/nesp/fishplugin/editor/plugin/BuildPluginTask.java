package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.editor.project.Project;

public class BuildPluginTask extends PluginBuildTask {

    @Override
    public String name() {
        return "Build";
    }

    @Override
    Result run(Project workingProject, OnPrintListener onPrintListener, Object... parameters) throws Exception {
        return Result.success();
    }

    @Override
    public PluginBuildTask[] dependencies() {
        return new PluginBuildTask[]{
                MoviePluginBuilder.getInstance().getPackagePluginTask()
        };
    }

}
