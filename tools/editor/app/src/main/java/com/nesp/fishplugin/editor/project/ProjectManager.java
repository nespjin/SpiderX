package com.nesp.fishplugin.editor.project;

import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.editor.utils.ZipUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

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

    public static Project createProject(String name, int pluginType) {
        if (!Project.isNameAvailable(name) || pluginType == -1) return null;
        Project project = new Project();
        Plugin targetPlugin = new Plugin();
        targetPlugin.setType(pluginType);
        project.setTargetPlugin(targetPlugin);
        project.setName(name);
        return project;
    }

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
}
