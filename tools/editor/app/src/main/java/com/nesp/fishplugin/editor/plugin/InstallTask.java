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
