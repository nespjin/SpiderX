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

import com.nesp.fishplugin.editor.DialogWorkingViewBinding;
import com.nesp.fishplugin.editor.R;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;

public class WorkingDialog<R1> extends AppBaseDialog<ButtonType> {

    private DoubleProperty progress;

    public DoubleProperty progressProperty() {
        if (progress == null) {
            progress = new SimpleDoubleProperty();
        }
        return progress;
    }

    public Double progress() {
        return progressProperty().get();
    }

    public void progress(double value) {
        progressProperty().set(value);
    }

    private Thread workingThread;
    private final ResultRunnable<R1> workingRunnable;
    private OnFinishListener<R1> onFinishListener;

    public WorkingDialog(ResultRunnable<R1> runnable) {
        workingRunnable = runnable;

        DialogWorkingViewBinding binding = DialogWorkingViewBinding.inflate(R.layout.dialog_working);
        setDialogPane(((DialogPane) binding.getRoot()));
        binding.lbTitle.textProperty().bind(titleProperty());
        binding.pbProgress.progressProperty().bind(progressProperty());
        progress(-1);
        Button button = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        if (button != null) {
            button.setText("Cancel");
        }

        setResultConverter(new Callback<ButtonType, ButtonType>() {
            @Override
            public ButtonType call(ButtonType param) {
                if (param == ButtonType.CANCEL) {
                    close();
                }
                return param;
            }
        });

        onCloseRequestProperty().addListener(new ChangeListener<EventHandler<DialogEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<DialogEvent>> observable,
                                EventHandler<DialogEvent> oldValue, EventHandler<DialogEvent> newValue) {
                interrupt();
            }
        });

        binding = null;
    }

    public void setOnFinishListener(OnFinishListener<R1> onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public void run() {
        final boolean[] isDone = {false};
        if (workingThread == null) {
            workingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    R1 r = null;
                    if (workingRunnable != null) {
                        try {
                            r = workingRunnable.run();
                        } catch (Exception e) {
                            LogManager.getLogger(WorkingDialog.class).error("error when run", e);
                        }
                    }
                    isDone[0] = true;

                    R1 finalR = r;
                    Platform.runLater(() -> {
                        hide();
                        if (onFinishListener != null) onFinishListener.onFinish(finalR);
                    });
                }
            });
        }
        workingThread.setDaemon(true);
        workingThread.start();
        long startWorkTimeMillis = System.currentTimeMillis();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isDone[0]) {
                    if (System.currentTimeMillis() - startWorkTimeMillis >= 3000) {
                        Platform.runLater(() -> show());
                        break;
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void interrupt() {
        if (workingThread != null) workingThread.interrupt();
    }

    public interface OnFinishListener<R> {
        void onFinish(R r);
    }
}
