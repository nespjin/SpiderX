package com.nesp.fishplugin.editor.plugin;

import com.google.gson.Gson;
import com.nesp.fishplugin.compiler.Compiler;
import com.nesp.fishplugin.editor.project.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CompilePluginTask extends PluginBuildTask {

    @Override
    public String name() {
        return "Compile Plugin";
    }

    @Override
    public Result run(Project workingProject, OnPrintListener onPrintListener, Object... parameters) throws Exception {
        Gson gson = new Gson();
        File projectManifestFile = workingProject.getProjectManifestFile();
        if (!projectManifestFile.exists()) {
            return Result.fail("Manifest file of project not exits");
        }
        Compiler.CompileResult compileResult;
        try {
            compileResult = Compiler.compileFromDisk(projectManifestFile.getPath());
        } catch (Exception e) {
            return Result.fail("Compile the file " + projectManifestFile.getName() +
                    " failed: \n" + e);
        }
        if (compileResult.getCode() != com.nesp.fishplugin.core.Result.CODE_SUCCESS) {
            String message = compileResult.getMessage();
            if (message.isEmpty())
                message = "Compile the file " + projectManifestFile.getName() + " failed";
            return Result.fail(message);
        }

        File midFile = new File(workingProject.getBuildCacheDirectory(), "plugin.out");
        if (!midFile.getParentFile().exists()) midFile.getParentFile().mkdirs();
        if (!midFile.exists() || midFile.delete()) {
            try {
                midFile.createNewFile();
                FileUtils.writeStringToFile(midFile, gson.toJson(compileResult.getData()));
            } catch (IOException e) {
                return Result.fail();
            }
        }
        workingProject.setTargetPlugin(compileResult.getData());
        return Result.success();
    }


    @Override
    public PluginBuildTask[] dependencies() {
        return new PluginBuildTask[0];
    }
}
