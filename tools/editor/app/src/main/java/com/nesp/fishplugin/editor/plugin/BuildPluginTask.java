package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.editor.project.Project;

public class BuildPluginTask implements PluginBuildTask {

    @Override
    public String name() {
        return "Build";
    }

    @Override
    public Result run(Project workingProject, Object... parameters) throws Exception {
        return null;
    }

    @Override
    public PluginBuildTask[] dependencies() {
        return new PluginBuildTask[]{new CompilePluginTask(), new PackagePluginTask()};
    }

}
