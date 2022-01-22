package com.nesp.fishplugin.editor.project;

import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.editor.app.Storage;
import com.nesp.sdk.java.text.TextUtil;

import java.io.File;

public class Project {

    private String name = "";
    private File rootDirectory = null;
    private Plugin targetPlugin = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) name = "";
        this.name = name;
        setRootDirectory(Storage.getProjectDir(name));
    }

    public static boolean isNameAvailable(String name) {
        return !TextUtil.isEmpty(name)
                && TextUtil.checkLength(name, 20);
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public File getSourceDirectory() {
        return new File(rootDirectory, "src");
    }

    public File getBuildDirectory() {
        return new File(rootDirectory, "build");
    }

    private void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public Plugin getTargetPlugin() {
        return targetPlugin;
    }

    public void setTargetPlugin(Plugin targetPlugin) {
        this.targetPlugin = targetPlugin;
    }
}
