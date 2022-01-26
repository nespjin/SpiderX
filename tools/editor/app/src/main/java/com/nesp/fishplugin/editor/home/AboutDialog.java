package com.nesp.fishplugin.editor.home;

import com.nesp.fishplugin.editor.AppInfo;
import com.nesp.fishplugin.editor.BuildConfig;
import com.nesp.fishplugin.editor.DialogAboutViewBinding;
import com.nesp.fishplugin.editor.R;
import com.nesp.fishplugin.editor.app.AppBaseDialog;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class AboutDialog extends AppBaseDialog<ButtonType> {

    public AboutDialog() {
        DialogAboutViewBinding binding = DialogAboutViewBinding.inflate(R.layout.dialog_about);
        setDialogPane(((DialogPane) binding.getRoot()));
        setTitle("关于 " + AppInfo.name);

        binding.title.setText(AppInfo.name + " V" + BuildConfig.VERSION_NAME);

        final String runtimeVersion = System.getProperty("java.runtime.version")
                + " "
                + System.getProperty("os.arch");
        binding.lbRuntimeVersion.setText(String.format(binding.lbRuntimeVersion.getText(), runtimeVersion));
        final String vm = System.getProperty("java.vm.name")
                + " by "
                + System.getProperty("java.vendor");
        binding.lbVm.setText(String.format(binding.lbVm.getText(), vm));

        binding = null;
    }

}
