package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.compiler.Compiler;
import com.nesp.fishplugin.core.Result;
import com.nesp.fishplugin.editor.App;
import com.nesp.fishplugin.editor.concurrent.AppThreadManager;
import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.editor.project.ProjectManager;
import com.nesp.fishplugin.packager.PluginFile;
import com.nesp.fishplugin.packager.binary.BinaryPluginFile;
import com.nesp.sdk.java.util.OnResultListener;
import javafx.animation.Transition;
import javafx.application.Platform;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class MoviePluginBuilder implements PluginBuilder {

    private Thread workThread;

    @Override
    public String[] getBuildTypes() {
        return new String[]{
                "Build",
                "Test Home Page:DSL",
                "Test Home Page:JS",
                "Test Category Page:DSL",
                "Test Category Page:JS",
                "Test Search Page:DSL",
                "Test Search Page:JS",
                "Test Detail Page:DSL",
                "Test Detail Page:JS",
        };
    }

    @Override
    public void build(int buildType, OnBuildProgressListener onBuildProgressListener) {
        String buildTypeString = getBuildTypes()[buildType];

        publishProgress(onBuildProgressListener, new ProgressData(-1, 0, String.format("Starting %s", buildTypeString)));

        Runnable task = new Runnable() {
            @Override
            public void run() {
                switch (buildType) {
                    case 0:
                        buildPlugin(onBuildProgressListener);
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;
                }
            }
        };

        stopBuild(null);

        workThread = new Thread(task);
        workThread.setDaemon(true);
        workThread.start();
    }

    private void buildPlugin(OnBuildProgressListener onBuildProgressListener) {
        Project workingProject = ProjectManager.getInstance().getWorkingProject();
        if (workingProject == null) {
            publishProgress(onBuildProgressListener, new ProgressData(-1, "No Opened Project"));
            return;
        }

        File projectManifestFile = workingProject.getProjectManifestFile();

        for (double i = 0; i < 0.25; i = i + 0.001) {
            publishProgress(onBuildProgressListener, new ProgressData(i));
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
                return;
            }
        }

        publishProgress(onBuildProgressListener, new ProgressData(0.25, 0, "Compiling plugin..."));

        if (Thread.currentThread().isInterrupted()) return;

        for (double i = 0.25; i < 0.5; i = i + 0.001) {
            publishProgress(onBuildProgressListener, new ProgressData(i));
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
                return;
            }
        }

        if (Thread.currentThread().isInterrupted()) return;

        Compiler.CompileResult compileResult = Compiler.compileFromDisk(projectManifestFile.getPath());
        if (compileResult.getCode() != Result.CODE_SUCCESS) {
            String message = compileResult.getMessage();
            if (message.isEmpty())
                message = "Compile the file " + projectManifestFile.getName() + " failed";
            publishProgress(onBuildProgressListener, new ProgressData(-1, message));
            return;
        }

        publishProgress(onBuildProgressListener, new ProgressData(0.5, 2, "Compile plugin success"));

        for (double i = 0.5; i < 0.75; i = i + 0.001) {
            publishProgress(onBuildProgressListener, new ProgressData(i));
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
                return;
            }
        }

        if (Thread.currentThread().isInterrupted()) return;

        publishProgress(onBuildProgressListener, new ProgressData(0.75, 0, "Start Package"));

        for (double i = 0.75; i < 1; i = i + 0.001) {
            publishProgress(onBuildProgressListener, new ProgressData(i));
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
                return;
            }
        }

        if (Thread.currentThread().isInterrupted()) return;

        File binaryFile = workingProject.getBuildBinaryFile("FishPlugin");
        PluginFile file = new BinaryPluginFile(binaryFile.getAbsolutePath());
        file.write();

        publishProgress(onBuildProgressListener, new ProgressData(1, 2, "Package plugin success"));

    }

    @Override
    public void stopBuild(OnResultListener<Boolean> onResultListener) {
        if (workThread != null) {
            workThread.interrupt();
        }
        if (onResultListener != null) {
            onResultListener.onResult(true);
        }
    }

    private void publishProgress(OnBuildProgressListener onBuildProgressListener, ProgressData progress) {
        if (onBuildProgressListener == null || progress == null) return;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                onBuildProgressListener.onProgress(progress.progress(), progress.lineType(), progress.line());
            }
        });
    }

}
