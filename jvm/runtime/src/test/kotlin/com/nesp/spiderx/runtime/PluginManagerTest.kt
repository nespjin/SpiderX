/*
 * Copyright (c) 2025.  NESP Technology.
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
package com.nesp.spiderx.runtime

import com.nesp.spiderx.runtime.model.Dataset
import com.nesp.spiderx.runtime.model.FakeJniWebView
import com.nesp.spiderx.runtime.model.ScreenType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


const val PLUGIN_JSON = """
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
      "url": "https://api.example.com/users",
      "url@compact": "https://api.example.com/users/compact",
      "url@medium": "https://api.example.com/users/medium",
      "url@expanded": "https://api.example.com/users/expanded",
      "js": "function parse(data) { return JSON.parse(data); }",
      "js@compact": "function parse_compact(data) { return JSON.parse(data); }",
      "js@medium": "function parse_medium(data) { return JSON.parse(data); }",
      "js@expanded": "function parse_expanded(data) { return JSON.parse(data); }",
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
      "url@compact": null,
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
"""

/**
 * @author <a href="mailto:1756404649@qq.com">JinZhaolu</a>
 **/
class PluginManagerTest {

    lateinit var pluginManager: PluginManager

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        pluginManager = PluginManager()
    }

    private fun initPluginManager() {
        val databasePath = "/home/jinzhaolu/DevelopmentProjects/SpiderX/jvm/runtime/build/plugin.db"
        pluginManager.init(databasePath, ScreenType.EXPANDED, false, FakeJniWebView::class.java)
    }

    @Test
    fun testInit() {
        val databasePath = "/home/jinzhaolu/DevelopmentProjects/SpiderX/jvm/runtime/build/plugin.db"
        pluginManager.init(databasePath, ScreenType.EXPANDED, false, FakeJniWebView::class.java)
    }

    @Test
    fun testInstallPluginJson() {
        initPluginManager()
        pluginManager.installPluginJson(PLUGIN_JSON)
        val isPluginInstalled = pluginManager.isPluginInstalled("com.example.plugin")
        assertTrue(isPluginInstalled)
    }

    @Test
    fun testRequestDataset() {
        initPluginManager()
        val ret = pluginManager.requestDataset("com.example.plugin", "user_data")
        println("testRequestDataset $ret")
    }

    @Test
    fun testGetInstalledPlugin() {
        initPluginManager()
        Dataset()
        val installedPlugin = pluginManager.getInstalledPlugin("com.example.plugin")
        println("testGetInstalledPlugin $installedPlugin")
    }

    @Test
    fun testGetInstalledPlugins() {
        initPluginManager()
        Dataset()
        val installedPlugins = pluginManager.getInstalledPlugins()
        println("testGetInstalledPlugins $installedPlugins")
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
//        TODO("Not yet implemented")
    }

}