package com.nesp.fishplugin.editor.project;

import com.nesp.fishplugin.editor.DialogNewProjectWizardViewBinding;
import com.nesp.fishplugin.editor.R;
import com.nesp.fishplugin.editor.app.AppAlert;
import com.nesp.fishplugin.editor.app.AppBaseDialog;
import com.nesp.fishplugin.editor.app.Storage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.util.Callback;

public class NewProjectWizardDialog extends AppBaseDialog<Project> {

    public NewProjectWizardDialog(int pluginType) {
        DialogNewProjectWizardViewBinding binding =
                DialogNewProjectWizardViewBinding.inflate(R.layout.dialog_new_project_wizard);
        setDialogPane(((DialogPane) binding.getRoot()));
        setTitle("New Project");

        Button buttonOk = ((Button) getDialogPane().lookupButton(ButtonType.OK));
        buttonOk.setText("Ok");
        buttonOk.setDisable(true);

        Button buttonCancel = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        buttonCancel.setText("Cancel");

        setResultConverter(new ResultConvert(binding, pluginType));

        binding.tfProjectLocation.setText(Storage.getProjectDirPath("").toString());

        ChangeListener<String> onProjectNameChangedListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                binding.tfProjectLocation.setText(Storage.getProjectDirPath(newValue).toString());
                buttonOk.setDisable(!Project.isNameAvailable(newValue));
            }
        };
        binding.tfProjectName.textProperty().addListener(new WeakChangeListener<>(onProjectNameChangedListener));
    }

    private static final class ResultConvert implements Callback<ButtonType, Project> {

        private final DialogNewProjectWizardViewBinding binding;
        private int pluginType = -1;

        public ResultConvert(DialogNewProjectWizardViewBinding binding, int pluginType) {
            this.binding = binding;
            this.pluginType = pluginType;
        }

        @Override
        public Project call(ButtonType param) {
            if (param == ButtonType.OK) {
                String name = binding.tfProjectName.getText();
                if (Project.isNameAvailable(name)) {
                    return ProjectManager.createProject(name, pluginType);
                } else {
                    Alert alert =
                            new AppAlert(Alert.AlertType.WARNING, "Project name is not available!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            return null;
        }
    }
}
