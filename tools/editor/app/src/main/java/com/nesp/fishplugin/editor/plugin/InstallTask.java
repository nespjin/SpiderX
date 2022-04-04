package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.installer.InstallException;

/**
 * Team: NESP Technology
 *
 * @author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/4/5 3:24 AM
 * Description:
 **/
public class InstallTask extends PluginBuildTask {

    @Override
    public String name() {
        return "Install Plugin";
    }

    @Override
    Result run(Project workingProject, OnPrintListener onPrintListener, Object... parameters) throws Exception {
        int deviceType = (int) getParameter("deviceType");
        try {
            new FpkInstaller(deviceType).install(workingProject.getTargetPlugin());
        } catch (InstallException e) {
            e.printStackTrace();
            return Result.fail("Install failed");
        }
        return Result.success();
    }

    @Override
    PluginBuildTask[] dependencies() {
        return new PluginBuildTask[] {
            MoviePluginBuilder.getInstance().getCompilePluginTask()
        } ;
    }
}
