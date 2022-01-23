package com.nesp.fishplugin.editor.app;

import com.nesp.sdk.java.text.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public final class Storage {

    private static final Logger LOGGER = LogManager.getLogger(Storage.class);

    private Storage() {
        //no instance
    }

    private static final String PROJECTS_DIR_NAME = "FishPluginProjects";

    public static File getProjectsDir() {
        return getDir(getProjectsDirPath().toString());
    }

    @NotNull
    public static Path getProjectsDirPath() {
        return Path.of(getUserHomePath(), PROJECTS_DIR_NAME);
    }

    public static File getProjectDir(String name) {
        if (TextUtil.isEmpty(name)) return null;
        return getDir(getProjectDirPath(name).toString());
    }

    @NotNull
    public static Path getProjectDirPath(String name) {
        return Path.of(getProjectsDir().getAbsolutePath(), name);
    }

    private static File getDir(String path) {
        if (path == null || path.isEmpty()) return null;
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LOGGER.info("The file for " + path + " mkdirs failed");
            }
        }
        return file;
    }

    public static String getUserTmpPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    public static String getAppExecPath() {
        return "./";
    }

}
