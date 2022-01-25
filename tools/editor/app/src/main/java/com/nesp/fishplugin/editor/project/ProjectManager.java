package com.nesp.fishplugin.editor.project;

import com.nesp.fishplugin.compiler.Compiler;
import com.nesp.fishplugin.compiler.Loader;
import com.nesp.fishplugin.core.Result;
import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.editor.utils.ZipUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

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
    public static void initializeProject(Project project) {
        copyProjectTemplate(project);
    }

    private static void copyProjectTemplate(Project project) {
        if (project == null || project.getTargetPlugin() == null
                || project.getTargetPlugin().getType() != Plugin.TYPE_MOVIE) {
            return;
        }
        String movieTemplateZipFileName = "movie-project.zip";
        URL resource = Thread.currentThread().getContextClassLoader()
                .getResource("template/" + movieTemplateZipFileName);
        if (resource == null) return;
        try {
            ZipUtil.unzip(new File(resource.toURI()), project.getRootDirectory());
        } catch (URISyntaxException ignored) {
        }
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
