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

package com.nesp.fishplugin.editor.plugin;

import com.nesp.sdk.java.util.OnResultListener;

import java.util.*;

public abstract class PluginBuilder {

    public static final int BUILD_STATUS_NONE = 0;
    public static final int BUILD_STATUS_START = 1;
    public static final int BUILD_STATUS_BUILDING = 2;
    public static final int BUILD_STATUS_FAILED = 3;
    public static final int BUILD_STATUS_SUCCESS = 4;
    public static final int BUILD_STATUS_STOP = 5;

    public static String getBuildStatusString(int status) {
        return switch (status) {
            case BUILD_STATUS_START -> "Start Build...";
            case BUILD_STATUS_BUILDING -> "Building...";
            case BUILD_STATUS_FAILED -> "Build Failed";
            case BUILD_STATUS_SUCCESS -> "Build Success";
            case BUILD_STATUS_STOP -> "Stop Build...";
            default -> "";
        };
    }

    public abstract PluginBuildTask[] getBuildTasks();

    public String[] getBuildTaskDisplayNames() {
        return Arrays.stream(getBuildTasks()).map(PluginBuildTask::name).toArray(String[]::new);
    }

    public abstract void build(int buildTaskIndex, OnBuildProgressListener onBuildProgressListener);

    public abstract void stopBuild(OnResultListener<Boolean> onResultListener);

    /**
     * @param task 任务
     * @return 任务链
     */
    public static List<List<PluginBuildTask>> analyseBuildTask(PluginBuildTask task) {
        List<List<PluginBuildTask>> r = new ArrayList<>();
        List<PluginBuildTask> dependencies = analyseBuildTaskDependenciesOnSameLevel(task);
        while (dependencies.size() > 0) {
            r.add(0, dependencies);
            dependencies = analyseBuildTaskDependenciesOnSameLevel(dependencies.toArray(new PluginBuildTask[0]));
        }

        List<String> taskNames = new ArrayList<>();
        for (List<PluginBuildTask> pluginBuildTasks : r) {
            int size = pluginBuildTasks.size();
            for (int j = size - 1; j >= 0; j--) {
                PluginBuildTask pluginBuildTask = pluginBuildTasks.get(j);

                List<PluginBuildTask> tasksToDelete = new ArrayList<>();
                for (String taskName : taskNames) {
                    if (taskName.equals(pluginBuildTask.name())) {
                        tasksToDelete.add(pluginBuildTask);
                    }
                }
                if (!tasksToDelete.isEmpty()) {
                    pluginBuildTasks.removeAll(tasksToDelete);
                } else {
                    taskNames.add(pluginBuildTask.name());
                }
            }
        }

        // Add root task
        r.add(new ArrayList<>() {
            {
                add(task);
            }
        });
        return r;
    }

    private static List<PluginBuildTask> analyseBuildTaskDependenciesOnSameLevel(PluginBuildTask... tasks) {
        if (tasks == null || tasks.length == 0) return null;
        List<PluginBuildTask> r = new ArrayList<>();
        for (PluginBuildTask task : tasks) {
            List<PluginBuildTask> dependencies =
                    task.dependencies() == null ? new ArrayList<>() : Arrays.asList(task.dependencies());
            if (dependencies.size() == 0) continue;

            for (PluginBuildTask dependency : dependencies) {
                List<PluginBuildTask> collect = r.stream().filter(task1 -> task1.name().equals(dependency.name())).toList();
                if (collect == null || collect.isEmpty()) r.add(dependency);
            }
        }
        return r;
    }

    public static int getTaskCountInChain(List<List<PluginBuildTask>> chain) {
        int count = 0;
        for (List<PluginBuildTask> pluginBuildTasks : chain) {
            count += pluginBuildTasks.size();
        }
        return count;
    }

}
