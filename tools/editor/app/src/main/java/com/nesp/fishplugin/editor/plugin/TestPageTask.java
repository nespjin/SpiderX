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

import com.nesp.fishplugin.core.data.Page2;
import com.nesp.fishplugin.core.data.Plugin2;
import com.nesp.fishplugin.editor.project.Project;
import com.nesp.fishplugin.runtime.Process;
import com.nesp.fishplugin.runtime.javafx.js.JavaFxJsRuntimeTaskListener;
import com.nesp.fishplugin.runtime.movie.javafx.MovieJavaFxJsRuntime;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TestPageTask extends PluginBuildTask {

    private final String pageId;
    private final int type;

    /**
     * @param pageId id of page
     * @param type   0 dsl, 1 js
     */
    public TestPageTask(String pageId, int type) {
        this.pageId = pageId;
        this.type = type;
    }

    @Override
    public String name() {
        String typeString = type == 0 ? "DSL" : "Js";
        String pageId2 = pageId.substring(0, 1).toUpperCase(Locale.ROOT) + pageId.substring(1);
        return "Test " + pageId2 + " Page" + ": " + typeString;
    }

    @Override
    public Result run(Project workingProject, OnPrintListener onPrintListener, Object... parameters) throws Exception {
        Plugin2 targetPlugin = workingProject.getTargetPlugin();
        if (targetPlugin == null) {
            return Result.fail("The target plugin of project not exists");
        }

        List<Page2> pages = new ArrayList<>();

        List<Page2> targetPluginPages = targetPlugin.getPages();
        for (Page2 targetPluginPage : targetPluginPages) {
            if (targetPluginPage.getId().startsWith(pageId)) {
                if (pageId.equals("search")) {
                    if (!targetPluginPage.getUrl().contains("@st")) {
                        return Result.fail("Not found keyword placeholder (@st) in plugin "+targetPlugin.getName()
                                +" for search page.");
                    }
                    Object keywordParam = getParameter("keyword");
                    if (keywordParam instanceof String && !((String) keywordParam).isEmpty()) {
                        targetPluginPage.setUrl(targetPluginPage.getUrl().replace("@st", (String) keywordParam));
                    }
                }
                pages.add(targetPluginPage);
            }
        }

        if (pages.isEmpty()) {
            return Result.fail("Not found page in target plugin.");
        }

        Optional<OnPrintListener> optOnPrintListener = Optional.ofNullable(onPrintListener);

        if (type == 1) {
            for (Page2 page : pages) {
                Object urlParam = getParameter("url");
                if (page.getUrl().isEmpty() && urlParam instanceof String && !((String) urlParam).isEmpty()) {
                    page.setUrl((String) urlParam);
                }
                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("Test " + page.getId() + " ..."));
                com.nesp.fishplugin.runtime.Process process =
                        new MovieJavaFxJsRuntime() {
                            {
                                setDeviceType((int) getParameter("deviceType"));
                            }
                        }.exec(page, new JavaFxJsRuntimeTaskListener() {
                            @Override
                            public void onPageLoadStart() {
                                super.onPageLoadStart();
//                                System.out.println("onPageLoadStart");
                                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("Start load page"));
                            }

                            @Override
                            public void onShouldInterceptRequest(@NotNull String url) {
                                super.onShouldInterceptRequest(url);
//                                System.out.println("onShouldInterceptRequest");
//                                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("onShouldInterceptRequest"));
                            }

                            @Override
                            public void onPageLoadFinished() {
                                super.onPageLoadFinished();
//                                System.out.println("onPageLoadFinished");
                                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("Page load finished"));
                            }

                            @Override
                            public void onReceiveError(@NotNull String error) {
                                super.onReceiveError(error);
//                                System.out.println("onReceiveError " + error
//                                        + "Thread Name = " + Thread.currentThread().getName());
                                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("Error occurred: " + error));
                            }

                            @Override
                            public void onReceivePage(@NotNull String page) {
                                super.onReceivePage(page);
                                System.out.println("onReceivePage page = " + page);
                                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("Page received:\n " + page + "\n"));
                            }

                            @Override
                            public void onTimeout() {
                                super.onTimeout();
//                                System.out.println("onTimeout");
                                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("Timeout"));
                            }

                            @Override
                            public void onPrintHtml(@NotNull String html) {
                                super.onPrintHtml(html);
//                                System.out.println("onPrintHtml html = " + html);
                                optOnPrintListener.ifPresent(onPrintListener1 -> onPrintListener1.print("Print Html:\n" + html + "\n"));
                            }
                        });

                System.out.println("execResult:Thread = " + Thread.currentThread().getName());
                Process.ExecResult execResult = process.waitFor();
                System.out.println("execResult: " + execResult.getExitValue());
                if (execResult.getExitValue() != Process.EXIT_VALUE_NORMAL) {
                    return Result.fail(execResult.getMessage());
                }
            }
            targetPlugin.applyPages();
            return Result.success();
        }
        return Result.fail();
    }

    @Override
    public PluginBuildTask[] dependencies() {
        return new PluginBuildTask[]{
                MoviePluginBuilder.getInstance().getInstallTask(),
        };
    }

}
