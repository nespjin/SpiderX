package com.nesp.fishplugin.editor.project;

import com.google.gson.Gson;
import com.nesp.fishplugin.compiler.Loader;
import com.nesp.fishplugin.core.Result;
import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.editor.utils.ZipUtil;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.w3c.dom.CDATASection;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public final class ProjectManager {

    private static final class SInstanceHolder {
        private static final ProjectManager sInstance = new ProjectManager();
    }

    public static ProjectManager getInstance() {
        return SInstanceHolder.sInstance;
    }

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
        Plugin targetPlugin = new Plugin();
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
                Plugin targetPlugin = project.getTargetPlugin();
                Gson gson = new Gson();
                Plugin plugin = gson.fromJson(s, Plugin.class);
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
        URL resource = Thread.currentThread().getContextClassLoader()
                .getResource("template/" + movieTemplateZipFileName);
        if (resource == null) return false;
        try {
            ZipUtil.unzip(new File(resource.toURI()), project.getRootDirectory());
        } catch (URISyntaxException ignored) {
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
        File projectManifestFile = Project.findProjectManifestFile(projectDir);
        if (projectManifestFile == null)
            return Result.fail("The file " + Project.PLUGIN_MANIFEST_FILE_NAME + " not found");

        Loader.LoadResult loadResult = Loader.loadPluginFromDisk(projectManifestFile.getPath());
        if (loadResult.getCode() != Result.CODE_SUCCESS) {
            String message = loadResult.getMessage();
            if (message.isEmpty())
                message = "Load the manifest file " + projectManifestFile.getName() + " failed";
            return Result.fail(message);
        }

        Plugin data = loadResult.getData();
        Project project = new Project();
        project.setName(projectDir.getName());
        project.setTargetPlugin(data);

        return Result.success(project);
    }


}
