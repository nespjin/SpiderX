package com.nesp.fishplugin.installer;

import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.core.data.Plugin2;

/**
 * Team: NESP Technology
 *
 * @author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2022/4/1 11:37 PM
 * Description:
 **/
public class Installer {

    public static void install(Plugin2 plugin) {
        int deviceType = Environment.getShared().getDeviceType();

        // Parent
        Object parent = plugin.getParent(deviceType);

        // Check parent is loaded or not
        if (parent != null && !(parent instanceof Plugin2)) {
            throw new IllegalArgumentException("The parent is not loaded");
        }

        if (parent != null) {
            plugin.setParent(parent);
        }

        // load Parent
    }


}
