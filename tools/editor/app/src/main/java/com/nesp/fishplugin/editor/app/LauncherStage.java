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

package com.nesp.fishplugin.editor.app;

import com.nesp.fishplugin.editor.R;
import com.nesp.fishplugin.editor.StageLauncherViewBinding;
import com.nesp.fishplugin.editor.home.HomeStage;
import com.nesp.sdk.java.lang.SingletonFactory;
import javafx.animation.Transition;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class LauncherStage extends AppBaseStage {
    private LauncherStage() {
        //no instance
    }

    private static boolean isShown = false;

    public static void showWindow() {
        if (isShown) return;
        var shared =
                SingletonFactory.getWeakInstance(LauncherStage.class, LauncherStage::new);
        shared.show();
        isShown = true;
    }

    public static void hideWindow() {
        LauncherStage launcherStage = SingletonFactory.getWeakInstance(LauncherStage.class);
        if (launcherStage != null) {
            launcherStage.getStage().hide();
        }
        SingletonFactory.removeWeakInstance(LauncherStage.class);
    }


    @Override
    public void onCreate(@NotNull Stage stage) {
        super.onCreate(stage);
        stage.setOpacity(0);
        setContent(getBinding().getRoot());
        initStyle(StageStyle.TRANSPARENT);
        setResizable(false);
        stage.setMinWidth(600);
        stage.setMaxWidth(600);
        stage.setMinHeight(370);
        stage.setMaxHeight(370);

        Transition hideTransition = new Transition() {
            {
                setCycleDuration(new Duration(500));
            }

            @Override
            protected void interpolate(double frac) {
                stage.setOpacity(1 - frac);
                if (frac == 1) {
                    HomeStage.showWindow();
                }
            }
        };

        Transition showTransition = new Transition() {
            {
                setCycleDuration(new Duration(500));
            }

            @Override
            protected void interpolate(double frac) {
                stage.setOpacity(frac);
                if (frac == 1) {
                    runOnUIThreadDelay(4000, new Runnable() {
                        @Override
                        public void run() {
                            hideTransition.play();
                        }
                    });
                }
            }
        };
        showTransition.play();
    }


    @Override
    public void onHidden(WindowEvent event) {
        super.onHidden(event);
        isShown = false;
        mBinding.clear();
    }

    private WeakReference<StageLauncherViewBinding> mBinding;

    @NotNull
    private StageLauncherViewBinding getBinding() {
        if (mBinding == null || mBinding.get() == null) {
            mBinding = new WeakReference<>(StageLauncherViewBinding.inflate(R.layout.stage_launcher));
        }
        return Objects.requireNonNull(mBinding.get());
    }
}
