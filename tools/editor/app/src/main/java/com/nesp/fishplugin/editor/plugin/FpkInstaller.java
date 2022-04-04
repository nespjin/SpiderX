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
