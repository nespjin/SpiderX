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

package com.nesp.fishplugin.sample.android

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nesp.spiderx.runtime.PluginManager
import com.nesp.spiderx.runtime.android.AndroidWebView
import com.nesp.spiderx.runtime.model.ScreenType
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()
    private val pluginManager: PluginManager = PluginManager.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databasePath = getDatabasePath("plugin.db").absolutePath


        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxJcefWebView.class);
        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxEmptyWebView.class);
        pluginManager.init(databasePath, ScreenType.EXPANDED, false, AndroidWebView::class.java)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.install_plugin).setOnClickListener {
            installPlugin()
        }
        findViewById<Button>(R.id.uninstall_plugin).setOnClickListener {
            uninstallPlugin()
        }
        findViewById<Button>(R.id.request_dataset).setOnClickListener {
            request()
        }
    }

    private fun installPlugin() {
        pluginManager.installPluginJson(PLUGIN_JSON)
    }

    private fun uninstallPlugin() {
        pluginManager.uninstallPlugin("com.example.plugin")
    }

    private fun request() {
        backgroundExecutor.execute {
            Log.d(TAG, "request: dataset")
            try {
                pluginManager.requestDataset("com.example.plugin", "user_data")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "request: finished")
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        private val PLUGIN_JSON: String = """
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
            
            """.trimIndent()

        init {
            System.loadLibrary("spiderx_runtime")
        }
    }

}