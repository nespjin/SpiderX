/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.compiler.Compiler;
import com.nesp.fishplugin.editor.project.Project;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CompilePluginTask extends PluginBuildTask {

    private final Logger logger = LogManager.getLogger(CompilePluginTask.class);

    @Override
    public String name() {
        return "Compile Plugin";
    }

    @Override
    public Result run(Project workingProject, OnPrintListener onPrintListener, Object... parameters) throws Exception {
        File projectManifestFile = workingProject.getProjectManifestFile();
        if (!projectManifestFile.exists()) {
            return Result.fail("Manifest file of project not exits");
        }
        Compiler.CompileResult compileResult;
        try {
            compileResult = Compiler.compileFromDisk(projectManifestFile.getPath());
        } catch (Exception e) {
            logger.error("run", e);
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
                FileUtils.writeStringToFile(midFile, compileResult.getData().getStore().toString());
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
