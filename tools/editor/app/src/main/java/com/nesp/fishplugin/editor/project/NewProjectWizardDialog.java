package com.nesp.fishplugin.editor.project;

import com.nesp.fishplugin.core.data.Plugin;
import com.nesp.fishplugin.editor.DialogNewProjectWizardViewBinding;
import com.nesp.fishplugin.editor.R;
import com.nesp.fishplugin.editor.app.AppAlert;
import com.nesp.fishplugin.editor.app.AppBaseDialog;
import com.nesp.fishplugin.editor.app.Storage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.*;
import javafx.util.Callback;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class NewProjectWizardDialog extends AppBaseDialog<Project> {

    public NewProjectWizardDialog(int pluginType) {
        DialogNewProjectWizardViewBinding binding =
                DialogNewProjectWizardViewBinding.inflate(R.layout.dialog_new_project_wizard);
        setDialogPane(((DialogPane) binding.getRoot()));
        setTitle("新建项目");

        Button buttonOk = ((Button) getDialogPane().lookupButton(ButtonType.OK));
        buttonOk.setDisable(true);

        setResultConverter(new ResultConvert(binding, pluginType));

        binding.tfProjectLocation.setText(Storage.getProjectDirPath("").toString());

        final boolean[] isUserInputPluginId = {false};
        ChangeListener<String> onProjectNameChangedListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                binding.tfProjectLocation.setText(Storage.getProjectDirPath(newValue).toString());
                invalidateButtonOk(binding, buttonOk);
                if (!isUserInputPluginId[0]) {
                    String pluginTypeString = "";
                    if (pluginType == Plugin.TYPE_MOVIE) {
                        pluginTypeString = "movie";
                    }
                    if (!pluginTypeString.isEmpty()) pluginTypeString = "." + pluginTypeString;
                    String newVal = newValue;
                    while (newVal.startsWith(".")) {
                        newVal = newVal.substring(1);
                    }

                    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
                    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
                    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                    format.setVCharType(HanyuPinyinVCharType.WITH_V);

                    try {
                        char[] chars = newVal.toCharArray();
                        StringBuilder sb = new StringBuilder();
                        String[] s;
                        for (char c : chars) {
                            if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
                                s = PinyinHelper.toHanyuPinyinStringArray(c, format);
                                if (s != null) {
                                    sb.append(s[0]);
                                    continue;
                                }
                            }
                            sb.append(c);
                           /* if ((i + 1 >= chars.length) || String.valueOf(chars[i + 1]).matches("[\\u4E00-\\u9FA5]+")) {
                                sb.append(separator);
                            }*/
                        }
                        newVal = sb.toString();
                    } catch (BadHanyuPinyinOutputFormatCombination ignored) {

                    }

                    if (!newVal.isEmpty()) newVal = "." + newVal;
                    binding.tfPluginId.setText("com.example.fishplugin" + pluginTypeString + newVal);
                }
            }
        };
        binding.tfProjectName.textProperty().addListener(new WeakChangeListener<>(onProjectNameChangedListener));

        ChangeListener<String> onPluginIdChangedListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                isUserInputPluginId[0] = binding.tfPluginId.isFocused();
                invalidateButtonOk(binding, buttonOk);
            }
        };
        binding.tfPluginId.textProperty().addListener(new WeakChangeListener<>(onPluginIdChangedListener));

        binding.cbPhone.setSelected(true);
        ChangeListener<Boolean> deviceTypeSelectChangedListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                invalidateButtonOk(binding, buttonOk);
            }
        };
        binding.cbPhone.selectedProperty().addListener(deviceTypeSelectChangedListener);
        binding.cbTable.selectedProperty().addListener(deviceTypeSelectChangedListener);
        binding.cbDesktop.selectedProperty().addListener(deviceTypeSelectChangedListener);
    }

    private void invalidateButtonOk(DialogNewProjectWizardViewBinding binding, Button buttonOk) {
        buttonOk.setDisable(
                !Project.isNameAvailable(binding.tfProjectName.getText())
                        || binding.tfPluginId.getText().isEmpty()
                        || (!binding.cbPhone.isSelected() && !binding.cbTable.isSelected() && !binding.cbDesktop.isSelected())
        );
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
                    Project project = ProjectManager.createProject(name, pluginType);
                    if (project == null) return null;
                    Plugin targetPlugin = project.getTargetPlugin();
                    targetPlugin.setId(binding.tfPluginId.getText());
                    targetPlugin.setType(pluginType);
                    int flags = 0;
                    if (binding.cbPhone.isSelected()) {
                        flags |= Plugin.DEVICE_FLAG_PHONE;
                    }
                    if (binding.cbTable.isSelected()) {
                        flags |= Plugin.DEVICE_FLAG_TABLE;
                    }
                    if (binding.cbPhone.isSelected()) {
                        flags |= Plugin.DEVICE_FLAG_PHONE;
                    }
                    if (flags == 0) return null;
                    targetPlugin.setDeviceFlags(flags);
                    return project;
                } else {
                    Alert alert =
                            new AppAlert(Alert.AlertType.WARNING, "项目名不可用!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            return null;
        }
    }
}
