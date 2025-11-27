package com.nesp.fishplugin.javafx.app;

import com.nesp.spiderx.runtime.PluginManager;
import com.nesp.spiderx.runtime.javafx.JavaFxWebView;
import com.nesp.spiderx.runtime.model.ScreenType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class HelloController {
    private static final Logger LOGGER = LogManager.getLogger(HelloController.class);

    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    @FXML
    public Label tvResult;

    private final PluginManager pluginManager = PluginManager.getInstance();

    public HelloController() {
        final String databasePath = new File(".").getAbsolutePath() + "/build/plugin.db";
        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxJcefWebView.class);
        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxEmptyWebView.class);
        pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxWebView.class);
    }

    @FXML
    public void onInstallClick(ActionEvent actionEvent) {
        pluginManager.installPluginJson(PLUGIN_JSON);
    }

    @FXML
    public void onUninstallClick(ActionEvent actionEvent) {
        pluginManager.uninstallPlugin("com.example.plugin");
    }

    @FXML
    public void onRequestClick(ActionEvent actionEvent) {
        backgroundExecutor.execute(() -> {
            LOGGER.debug("Request dataset");
            try {
                pluginManager.requestDataset("com.example.plugin", "user_data");
            } catch (Exception e) {
                LOGGER.error("error when request dataset", e);
            }
            LOGGER.debug("Request dataset finished");
        });
    }

    private static final String PLUGIN_JSON = """
            {
              "parent": null,
              "id": "com.example.plugin",
              "name": "Test Plugin",
              "author": "Test Author",
              "version": "1.0.0",
              "runtimeVersion": "1.0",
              "description": "A test plugin",
              "tags": [],
              "supportedScreenTypes": [],
              "variables": {},
              "dataset": [
                {
                  "id": "user_data",
                  "url": "https://silidm.com/",
                  "url@compact": "https://silidm.com/",
                  "url@medium": "https://silidm.com/",
                  "url@expanded": "https://silidm.com/",
                  "js": "function parse(data) { return JSON.parse(data); }",
                  "js@compact": "function parse_compact(data) { return JSON.parse(data); }",
                  "js@medium": "function parse_medium(data) { return JSON.parse(data); }",
                  "js@expanded": "(function parse_expanded(data) { return 1; })();",
                  "dsl": {
                    "fields": [
                      "name",
                      "age"
                    ],
                    "parser": "json"
                  },
                  "dsl@compact": {
                    "fields@compact": [
                      "name",
                      "age"
                    ],
                    "parser@compact": "json"
                  },
                  "dsl@medium": {
                    "fields@medium": [
                      "name",
                      "age"
                    ],
                    "parser@medium": "json"
                  },
                  "dsl@expanded": {
                    "fields@expanded": [
                      "name",
                      "age"
                    ],
                    "parser@expanded": "json"
                  }
                },
                {
                  "id": "product_data",
                  "url": "https://api.example.com/products",
                  "url@compact": "https://api.example.com/products",
                  "url@medium": null,
                  "url@expanded": null,
                  "js": null,
                  "js@compact": null,
                  "js@medium": null,
                  "js@expanded": null,
                  "dsl": {
                    "fields": [
                      "name",
                      "price"
                    ],
                    "parser": "json"
                  },
                  "dsl@compact": null,
                  "dsl@medium": null,
                  "dsl@expanded": null
                }
              ]
            }
            """;
}