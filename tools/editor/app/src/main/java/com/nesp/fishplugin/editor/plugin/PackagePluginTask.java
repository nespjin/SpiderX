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

import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.core.data.Plugin2;
import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.packager.PluginFile;
import com.nesp.fishplugin.packager.binary.BinaryPluginFile;

import java.io.File;
import java.io.IOException;

public class PackagePluginTask extends PluginBuildTask {

    @Override
    public String name() {
        return "Package Plugin";
    }

    @Override
    public Result run(Project workingProject, OnPrintListener onPrintListener, Object... parameters) throws Exception {
        File binaryFile = workingProject.getBuildBinaryFile("plugin");
        PluginFile file = new BinaryPluginFile(binaryFile.getAbsolutePath());
        try {
            file.write(new Plugin2[]{workingProject.getTargetPlugin()});
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("Package plugin failed");
        }

        Result result = Result.success("Package plugin success");
        result.printMessages().add(Result.msg("Build Out Path: " + binaryFile.getAbsolutePath()));
        return result;
    }

    @Override
    public PluginBuildTask[] dependencies() {
        return new PluginBuildTask[]{
                MoviePluginBuilder.getInstance().getCompilePluginTask(),
                MoviePluginBuilder.getInstance().getInstallTask(),
                MoviePluginBuilder.getInstance().getTestHomePageJsTask(),
                MoviePluginBuilder.getInstance().getTestCategoryPageJsTask(),
                MoviePluginBuilder.getInstance().getTestSearchPageJsTask(),
                MoviePluginBuilder.getInstance().getTestDetailPageJsTask(),
        };
    }
}
