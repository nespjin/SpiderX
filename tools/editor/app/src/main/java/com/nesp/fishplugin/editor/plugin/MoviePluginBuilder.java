package com.nesp.fishplugin.editor.plugin;

import com.google.common.util.concurrent.AtomicDouble;
import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.editor.project.ProjectManager;
import com.nesp.sdk.java.util.OnResultListener;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MoviePluginBuilder extends PluginBuilder {

    private Thread workThread;
    private PluginBuildTask[] pluginBuildTasks;

    private static final class SInstanceHolder {
        private static final MoviePluginBuilder sInstance = new MoviePluginBuilder();
    }

    public static MoviePluginBuilder getInstance() {
        return SInstanceHolder.sInstance;
    }

    private MoviePluginBuilder() {
    }

    private final PluginBuildTask buildPluginTask = new BuildPluginTask();
    private final PluginBuildTask compilePluginTask = new CompilePluginTask();
    private final PluginBuildTask packagePluginTask = new PackagePluginTask();
    private final PluginBuildTask testHomePageJsTask = new TestPageTask("home", 1);
    private final PluginBuildTask testCategoryPageJsTask = new TestPageTask("category", 1);
    private final PluginBuildTask testSearchPageJsTask = new TestPageTask("search", 1) {
        {
            putParameter("keyword", "雪");
        }
    };
    private final PluginBuildTask testDetailPageJsTask = new TestPageTask("detail", 1) {
        {
            putParameter("url", "https://www.bei5dy.com/voddetail/96535/");
        }
    };

    public PluginBuildTask getBuildPluginTask() {
        return buildPluginTask;
    }

    public PluginBuildTask getCompilePluginTask() {
        return compilePluginTask;
    }

    public PluginBuildTask getPackagePluginTask() {
        return packagePluginTask;
    }

    public PluginBuildTask getTestHomePageJsTask() {
        return testHomePageJsTask;
    }

    public PluginBuildTask getTestCategoryPageJsTask() {
        return testCategoryPageJsTask;
    }

    public PluginBuildTask getTestSearchPageJsTask() {
        return testSearchPageJsTask;
    }

    public PluginBuildTask getTestDetailPageJsTask() {
        return testDetailPageJsTask;
    }

    @Override
    public PluginBuildTask[] getBuildTasks() {
        if (pluginBuildTasks == null) {
            pluginBuildTasks = new PluginBuildTask[]{
                    getBuildPluginTask(),
//                new TestPageTask("home", 0),
                    getTestHomePageJsTask(),
//                new TestPageTask("category", 0),
                    getTestCategoryPageJsTask(),
//                new TestPageTask("search", 0),
                    getTestSearchPageJsTask(),
//                new TestPageTask("detail", 0),
                    getTestDetailPageJsTask(),
            };
        }
        return pluginBuildTasks;
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
                System.out.println("buildTaskChain = " + buildTaskChain);
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
//                    CountDownLatch countDownLatch = new CountDownLatch(pluginBuildTasks.size());
                    for (PluginBuildTask pluginBuildTask : pluginBuildTasks) {
//                        Thread thread = new Thread(() -> {
                        boolean isSuccessLocal;
                        String msg = "";
                        PluginBuildTask.Result result = null;
                        try {
                            ProgressData progressData1 = new ProgressData(-2, 0,
                                    "Start " + pluginBuildTask.name());
                            publishProgress(onBuildProgressListener, progressData1);

                            result = pluginBuildTask.run(workingProject, new PluginBuildTask.OnPrintListener() {
                                @Override
                                public void print(String message) {
                                    publishProgress(onBuildProgressListener, new ProgressData(-2, 0, message));
                                }
                            });

                            isSuccessLocal = result.isSuccess();

                            msg = result.msg();

                            if (msg.isEmpty()) {
                                msg = pluginBuildTask.name() + " " + (isSuccessLocal ? "Success" : "Failed");
                            }

                        } catch (Throwable e) {
                            isSuccessLocal = false;
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
                        progressData1 = new ProgressData(progress, isSuccessLocal ? 2 : -1, msg);
                        publishProgress(onBuildProgressListener, progressData1);

                        publishProgress(onBuildProgressListener, new ProgressData(-2, 0,
                                "End " + pluginBuildTask.name()));

                        isSuccess.getAndSet(isSuccess.get() && isSuccessLocal);
//                            countDownLatch.countDown();
//                        });
//                        thread.setDaemon(true);
//                        thread.start();
                    }
                    /*try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }*/

                    if (!isSuccess.get()) break;
                }
                ProgressData progressData1 = new ProgressData(1, isSuccess.get() ? 2 : -1,
                        buildTask.name() + (isSuccess.get() ? " Success" : " Failed"));
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
}
