package com.nesp.fishplugin.editor.project;

import com.google.gson.Gson;
import com.nesp.fishplugin.compiler.Loader;
import com.nesp.fishplugin.core.Environment;
import com.nesp.fishplugin.core.Result;
import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.core.data.Plugin2;
import com.nesp.fishplugin.editor.app.Storage;
import com.nesp.fishplugin.editor.plugin.MoviePluginBuilder;
import com.nesp.fishplugin.editor.utils.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ProjectManager {

    private static final class SInstanceHolder {
        private static final ProjectManager sInstance = new ProjectManager();
    }

    public static ProjectManager getInstance() {
        return SInstanceHolder.sInstance;
    }

    private final Logger logger = LogManager.getLogger(ProjectManager.class);

    private Project workingProject = null;

    public Project getWorkingProject() {
        return workingProject;
    }

    public void setWorkingProject(Project workingProject) {
        this.workingProject = workingProject;
    }

    /**
     * Create New Project
     *
     * @param name       the project name.
     * @param pluginType the target plugin type.
     * @return project created.
     */
    public static Project createProject(String name, int pluginType) {
        if (!Project.isNameAvailable(name) || pluginType == -1) return null;
        Project project = new Project();
        Plugin2 targetPlugin = new Plugin2();
        targetPlugin.setName(name);
        targetPlugin.setType(pluginType);
        project.setTargetPlugin(targetPlugin);
        project.setName(name);
        return project;
    }

    /**
     * Call when project first created.
     *
     * @param project project
     */
    public static boolean initializeProject(Project project) {
        boolean b = copyProjectTemplate(project);
        if (b) {
            File projectManifestFile = project.getProjectManifestFile();
            try {
                String s = FileUtils.readFileToString(projectManifestFile);
                Plugin2 targetPlugin = project.getTargetPlugin();
                Gson gson = new Gson();
                Plugin2 plugin = new Plugin2(new JSONObject(s));
                plugin.setName(targetPlugin.getName());
                plugin.setId(targetPlugin.getId());
                plugin.setType(targetPlugin.getType());
                plugin.setDeviceFlags(targetPlugin.getDeviceFlags());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateNow = dateFormat.format(new Date());
                plugin.setTime(dateNow + "," + dateNow);
                project.setTargetPlugin(plugin);

                Gson gson1 = gson.newBuilder().setPrettyPrinting().create();
                String s1 = gson1.toJson(plugin);
                FileUtils.writeStringToFile(projectManifestFile, s1);
            } catch (IOException e) {
                LogManager.getLogger(ProjectManager.class).error("initializeProject failed", e);
                return false;
            }
        }
        return b;
    }

    private static boolean copyProjectTemplate(Project project) {
        if (project == null || project.getTargetPlugin() == null
                || project.getTargetPlugin().getType() != Plugin.TYPE_MOVIE) {
            return false;
        }
        String movieTemplateZipFileName = "movie-project.zip";
        InputStream resource = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("template/" + movieTemplateZipFileName);
        if (resource == null) return false;
        try {
            File movieTemplateFileTmp =
                    new File(Storage.getProjectsDir(), movieTemplateZipFileName);
            FileUtils.writeByteArrayToFile(movieTemplateFileTmp, resource.readAllBytes());
            ZipUtil.unzip(movieTemplateFileTmp, project.getRootDirectory());
            movieTemplateFileTmp.delete();
        } catch (IOException e) {
            LogManager.getLogger(ProjectManager.class).error("copyProjectTemplate failed", e);
            return false;
        }
        return true;
    }

    /**
     * May block
     *
     * @param projectDir projectDir
     * @return result
     */
    public static Result<Project> openProject(File projectDir) {
        Project project = new Project();
        project.setName(projectDir.getName());
        Result<Boolean> switchDeviceType = switchDeviceType(project, -1);
        if (switchDeviceType.getCode() == Result.CODE_FAILED) {
            return Result.fail(switchDeviceType.getMessage());
        }
        return Result.success(project);
    }

    public static Result<Boolean> switchDeviceType(Project project, int deviceType) {
        if (project == null)
            return Result.fail("switchDeviceType the project is null.");

        File projectManifestFile = project.getProjectManifestFile();
        if (projectManifestFile == null)
            return Result.fail("The file " + Project.PLUGIN_MANIFEST_FILE_NAME + " not found");

        Loader.LoadResult loadResult = Loader.loadPluginFromDisk(projectManifestFile.getPath());
        if (loadResult.getCode() != Result.CODE_SUCCESS) {
            String message = loadResult.getMessage();
            if (message.isEmpty())
                message = "Load the manifest file " + projectManifestFile.getName() + " failed";
            return Result.fail(message);
        }

        Plugin2 targetPlugin = loadResult.getData();
        if (targetPlugin == null) {
            return Result.fail("switchDeviceType targetPlugin is null");
        }

        if (deviceType < 0) {
            if (targetPlugin.isSupportMobilePhone()) {
                return switchDeviceType(project, Environment.DEVICE_TYPE_MOBILE_PHONE);
            } else if (targetPlugin.isSupportTable()) {
                return switchDeviceType(project, Environment.DEVICE_TYPE_TABLE);
            } else if (targetPlugin.isSupportDesktop()) {
                return switchDeviceType(project, Environment.DEVICE_TYPE_DESKTOP);
            }
            return Result.fail("switchDeviceType device type is not supported");
        }

        MoviePluginBuilder.getInstance().setDeviceType(deviceType);
        project.setTargetPlugin(targetPlugin);

        return Result.success(true);
    }
}
