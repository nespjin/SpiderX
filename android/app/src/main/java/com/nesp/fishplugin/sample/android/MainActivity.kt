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
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nesp.spiderx.runtime.PluginManager
import com.nesp.spiderx.runtime.RequestJavaScriptDatasetListener
import com.nesp.spiderx.runtime.android.AndroidWebView
import com.nesp.spiderx.runtime.model.ScreenType
import org.apache.logging.log4j.core.util.internal.HttpInputStreamUtil.readStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()
    private val pluginManager: PluginManager = PluginManager.instance
    private val mainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> if (msg.obj is String) tvResult.text = msg.obj as String
            }
        }
    }
    lateinit var tvResult: TextView
    lateinit var etDatasetId: EditText

    private val requestJavaScriptDatasetListener = object : RequestJavaScriptDatasetListener() {
        override fun onPageStarted(url: String) {
            Log.d(TAG, "request: page started $url")
        }

        override fun onLoadProgress(url: String, progress: Int) {
            Log.d(TAG, "request: load progress $url $progress")
        }

        override fun onPageFinished(url: String, document: String) {
            Log.d(TAG, "request: page finished $url $document")
        }

        override fun onShouldInterceptRequest(url: String) {
            Log.d(TAG, "onShouldInterceptRequest: $url")
        }

        override fun onShouldOverrideUrlLoading(url: String) {
            Log.d(TAG, "onShouldOverrideUrlLoading: $url")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pluginManager.setAndroidContext(applicationContext)
        val databasePath = getDatabasePath("plugin.db").absolutePath


        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxJcefWebView.class);
        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxEmptyWebView.class);
        pluginManager.init(databasePath, ScreenType.EXPANDED, false, AndroidWebView::class.java)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.install_plugin).setOnClickListener { installPlugin() }
        findViewById<Button>(R.id.uninstall_plugin).setOnClickListener { uninstallPlugin() }
        findViewById<Button>(R.id.request_dataset).setOnClickListener { request() }
        tvResult = findViewById(R.id.tv_result)
        etDatasetId = findViewById(R.id.et_dataset_id)
    }

    private fun installPlugin() {
        val pluginJson = readStream(assets.open("plugin.json")).toString(Charsets.UTF_8)
        pluginManager.installPluginJson(pluginJson)
    }

    private fun uninstallPlugin() {
        pluginManager.uninstallPlugin("com.example.plugin")
    }

    private fun request() {
        val datasetId = etDatasetId.text.toString()
        backgroundExecutor.execute {
            Log.d(TAG, "request: dataset $datasetId")
            try {
                val result = pluginManager.requestDataset(
                    "com.example.plugin",
                    datasetId,
                    type = PluginManager.RequestType.JavaScript,
                    listener = requestJavaScriptDatasetListener
                )
                Log.d(TAG, "request: result $result")
                mainHandler.obtainMessage(0, result).sendToTarget()
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.obtainMessage(0, e.message).sendToTarget()
            }
            Log.d(TAG, "request: finished")
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        init {
            System.loadLibrary("spiderx_runtime")
        }
    }

}