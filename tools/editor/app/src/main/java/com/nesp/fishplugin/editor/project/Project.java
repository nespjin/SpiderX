package com.nesp.fishplugin.editor.project;

import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.editor.app.Storage;
import com.nesp.sdk.java.text.TextUtil;

import java.io.File;
import java.nio.file.Path;

public class Project {

    public static final String SRC_DIR_NAME = "src";
    public static final String BUILD_DIR_NAME = "build";
    public static final String BUILD_OUT_DIR_NAME = "outputs";
    private static final String BUILD_CACHE_DIR_NAME = "cache";
    public static final String PLUGIN_MANIFEST_FILE_NAME = "PluginManifest";
    public static final String[] PLUGIN_MANIFEST_FILE_EXTENSIONS = {"json"};
    private static final String FISH_PLUGIN_FILE_EXTENSION = ".fpk";

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

    public File getProjectManifestFile() {
        return findProjectManifestFile(getRootDirectory());
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public File getSourceDirectory() {
        return Storage.getDir(Path.of(rootDirectory.getAbsolutePath(), SRC_DIR_NAME).toString());
    }

    public File getBuildDirectory() {
        return Storage.getDir(Path.of(rootDirectory.getAbsolutePath(), BUILD_DIR_NAME).toString());
    }

    public File getBuildOutDirectory() {
        return Storage.getDir(Path.of(getBuildDirectory().getAbsolutePath(), BUILD_OUT_DIR_NAME).toString());
    }

    public File getBuildCacheDirectory() {
        return Storage.getDir(Path.of(getBuildDirectory().getAbsolutePath(), BUILD_CACHE_DIR_NAME).toString());
    }

    public File getBuildBinaryFile(String binaryName) {
        return new File(getBuildOutDirectory().getAbsolutePath(), binaryName + FISH_PLUGIN_FILE_EXTENSION);
    }

    private void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    };

    public Plugin getTargetPlugin() {
        return targetPlugin;
    }

    public void setTargetPlugin(Plugin targetPlugin) {
        this.targetPlugin = targetPlugin;
    }

    public static File findProjectManifestFile(File projectDir) {
        if (projectDir == null || projectDir.isFile()) return null;
        File[] files = projectDir.listFiles();
        if (files == null || files.length == 0) return null;
        for (String pluginManifestFileExtension : Project.PLUGIN_MANIFEST_FILE_EXTENSIONS) {
            File file = Path.of(projectDir.getAbsolutePath(), Project.SRC_DIR_NAME,
                    Project.PLUGIN_MANIFEST_FILE_NAME + "." + pluginManifestFileExtension).toFile();
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}
