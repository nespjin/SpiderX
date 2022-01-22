package com.nesp.fishplugin.editor;

import com.nesp.fishplugin.editor.home.HomeStage;
import com.nesp.sdk.javafx.ApplicationContext;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.helpers.Loader;

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/12/2 上午8:53
 * Description:
 **/
public class App extends ApplicationContext {

    private static final String TAG = "Main";

    @Override
    public void start(final Stage primaryStage) throws Exception {
        HomeStage.showWindow();
        primaryStage.hide();
    }

    public static void main(String[] args) {
         Application.launch(App.class, args);
    }
}
