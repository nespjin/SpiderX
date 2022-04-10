/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.fishplugin.editor.home;

import com.nesp.fishplugin.editor.*;
import com.nesp.fishplugin.editor.app.AppBaseDialog;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;

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
        DialogAboutViewBinding finalBinding = binding;
        binding.hlOpenSource.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                App.getInstance().getHostServices().showDocument(finalBinding.hlOpenSource.getText());
            }
        });

        binding = null;
    }

}
