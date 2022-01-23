package com.nesp.fishplugin.editor.home;

import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.editor.project.ProjectManager;
import javafx.beans.property.*;

public class HomeStateViewModel {

    HomeStateViewModel() {

    }

    private ObjectProperty<Project> workingProject;

    public ObjectProperty<Project> workingProjectProperty() {
        if (workingProject == null) {
            workingProject = new SimpleObjectProperty<>() {
                @Override
                protected void invalidated() {
                    onWorkingProjectInvalidate(get());
                }
            };
        }
        return workingProject;
    }

    public Project workingProject() {
        return workingProjectProperty().get();
    }

    public void workingProject(Project value) {
        ProjectManager.getInstance().setWorkingProject(value);
        workingProjectProperty().set(value);
    }

    void onWorkingProjectInvalidate(Project project) {

    }

    private StringProperty bottomStatus;

    public StringProperty bottomStatusProperty() {
        if (bottomStatus == null) {
            bottomStatus = new SimpleStringProperty() {
                @Override
                protected void invalidated() {
                    onBottomStatusInvalidate(get());
                }
            };
        }
        return bottomStatus;
    }

    public String bottomStatus() {
        return bottomStatusProperty().get();
    }

    public void bottomStatus(String value) {
        bottomStatusProperty().set(value);
    }

    void onBottomStatusInvalidate(String status) {

    }

    void destroy() {
        workingProject.unbind();
    }

}
