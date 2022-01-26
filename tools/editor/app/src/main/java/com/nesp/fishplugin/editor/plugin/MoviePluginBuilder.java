package com.nesp.fishplugin.editor.plugin;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.editor.project.ProjectManager;
import com.nesp.sdk.java.util.OnResultListener;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MoviePluginBuilder extends PluginBuilder {

    private final Logger logger = LogManager.getLogger(MoviePluginBuilder.class);
    private Thread workThread;
    private final Gson gson = new Gson();

    @Override
    public PluginBuildTask[] getBuildTasks() {
        return new PluginBuildTask[]{
                new BuildPluginTask(),
                new TestPageTask("home", 0),
                new TestPageTask("home", 1),
                new TestPageTask("category", 0),
                new TestPageTask("category", 1),
                new TestPageTask("search", 0),
                new TestPageTask("search", 1),
                new TestPageTask("detail", 0),
                new TestPageTask("detail", 1),
        };
    }

    @Override
    public void build(int buildTaskIndex, OnBuildProgressListener onBuildProgressListener) {
        PluginBuildTask buildTask = getBuildTasks()[buildTaskIndex];
        ProgressData progressData =
                new ProgressData(-1, 0, String.format("Start %s", buildTask.name()));
        publishProgress(onBuildProgressListener, progressData);

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                final Project workingProject = ProjectManager.getInstance().getWorkingProject();
                ProgressData progressData;
                if (workingProject == null) {
                    progressData = new ProgressData(-1, "No Opened Project");
                    publishProgress(onBuildProgressListener, progressData);
                    return;
                }
                List<List<PluginBuildTask>> buildTaskChain = analyseBuildTask(buildTask);
                final int taskCountInChain = getTaskCountInChain(buildTaskChain);
                AtomicInteger taskCountRun = new AtomicInteger(0);
                if (buildTaskChain.isEmpty()) {
                    progressData = new ProgressData(-1, "错误: 任务链为空");
                    publishProgress(onBuildProgressListener, progressData);
                    return;
                }

                AtomicBoolean isSuccess = new AtomicBoolean(true);
                AtomicDouble lastProgress = new AtomicDouble(0);

                for (List<PluginBuildTask> pluginBuildTasks : buildTaskChain) {
                    CountDownLatch countDownLatch = new CountDownLatch(pluginBuildTasks.size());
                    for (PluginBuildTask pluginBuildTask : pluginBuildTasks) {
                        Thread thread = new Thread(() -> {
                            boolean isSuccess1;
                            String msg = "";
                            PluginBuildTask.Result result = null;
                            try {
                                ProgressData progressData1 = new ProgressData(-2, 0,
                                        "Start " + pluginBuildTask.name());
                                publishProgress(onBuildProgressListener, progressData1);

                                result = pluginBuildTask.run(workingProject);

                                isSuccess1 = result.code() == PluginBuildTask.Result.CODE_SUCCESS;
                                isSuccess.getAndSet(isSuccess.get() && isSuccess1);

                                msg = result.msg();

                                if (msg.isEmpty()) {
                                    msg = pluginBuildTask.name() + " " + (isSuccess1 ? "Success" : "Failed");
                                }

                            } catch (Exception e) {
                                isSuccess1 = false;
                                isSuccess.getAndSet(false);
                            }

                            double progress = (taskCountRun.incrementAndGet() * 1.00 / taskCountInChain) * 0.9;
                            for (double i = lastProgress.get(); i < progress; i += 0.001) {
                                try {
                                    ProgressData progressData1 = new ProgressData(i, 0, "");
                                    publishProgress(onBuildProgressListener, progressData1);
                                    Thread.sleep(1);
                                } catch (InterruptedException ignored) {
                                }
                            }

                            lastProgress.set(progress);
                            ProgressData progressData1;

                            if (result == null) {
                                result = PluginBuildTask.Result.fail("Unknown Error");
                            }
                            if (!result.printMessages().isEmpty()) {
                                for (PluginBuildTask.Result printMessage : result.printMessages()) {
                                    progressData1 = new ProgressData(-2, 0, printMessage.msg());
                                    publishProgress(onBuildProgressListener, progressData1);
                                }
                            }
                            progressData1 = new ProgressData(progress, isSuccess1 ? 2 : -1, msg);
                            publishProgress(onBuildProgressListener, progressData1);

                            countDownLatch.countDown();
                        });
                        thread.setDaemon(true);
                        thread.start();
                    }
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                ProgressData progressData1 = new ProgressData(1, isSuccess.get() ? 2 : -1, "");
                publishProgress(onBuildProgressListener, progressData1);
            }
        };

        stopBuild(null);

        workThread = new Thread(task);
        workThread.setDaemon(true);
        workThread.start();
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

    private void testPageWithDsl(String pageId) {

    }

    private void testPageWithJs() {

    }

}
