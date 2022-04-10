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

import com.nesp.fishplugin.core.data.Plugin2;
import com.nesp.fishplugin.installer.InstallException;
import com.nesp.fishplugin.installer.Installer;

/**
 * Team: NESP Technology
 *
 * @author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/4/5 3:25 AM
 * Description:
 **/
public class FpkInstaller extends Installer {

    private final int deviceType;

    public FpkInstaller(int deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    protected void doInstall(Plugin2 plugin) throws InstallException {

    }

    @Override
    public int getEnvironmentDeviceType() {
        return deviceType;
    }
}
