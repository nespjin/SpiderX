/*
 * Copyright (c) 2026.  NESP Technology.
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

package com.nesp.spiderx.android.example

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.nesp.spiderx.android.example.R
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
    private lateinit var tvResult: TextView
    private lateinit var etReqPluginId: EditText
    private lateinit var etReqDatasetId: EditText
    private lateinit var etUninstallPluginId: EditText
    // private lateinit var wvTest: WebView

    private lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = getSharedPreferences("plugin", MODE_PRIVATE)

        pluginManager.setAndroidContext(applicationContext)
        val databasePath = getDatabasePath("plugin.db").absolutePath

        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxJcefWebView.class);
        // pluginManager.init(databasePath, ScreenType.EXPANDED, false, JavaFxEmptyWebView.class);
        pluginManager.init(databasePath, ScreenType.EXPANDED, false, AndroidWebView::class.java)

        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.install_plugin).setOnClickListener { installPlugin() }
        findViewById<Button>(R.id.uninstall_plugin).setOnClickListener { uninstallPlugin() }
        findViewById<Button>(R.id.request_dataset).setOnClickListener { request() }
        findViewById<Button>(R.id.get_installed_plugins).setOnClickListener { getInstalledPlugins() }

        tvResult = findViewById(R.id.tv_result)
        // initWebView()
        etReqPluginId = findViewById(R.id.et_req_plugin_id)
        etReqDatasetId = findViewById(R.id.et_req_dataset_id)
        etUninstallPluginId = findViewById(R.id.et_uninstall_plugin_id)

        val sharPer = sharedPreferences
        etReqPluginId.setText(sharPer.getString(SHAR_PER_KEY_REQ_PLUGIN_ID, ""))
        etReqDatasetId.setText(sharPer.getString(SHAR_PER_KEY_REQ_DATASET_ID, ""))
        etUninstallPluginId.setText(sharPer.getString(SHAR_PER_KEY_UNINSTALL_PLUGIN_ID, ""))
    }

    // private fun initWebView() {
    //     wvTest = findViewById(R.id.wv_test)
    //     wvTest.apply {
    //         // visibility = WebView.INVISIBLE
    //         // layoutParams = LinearLayout.LayoutParams(1, 1)
    //         clearFocus()
    //         settings.defaultTextEncodingName = "utf-8"
    //         settings.userAgentString = pluginManager.getUserAgent()
    //         settings.cacheMode = WebSettings.LOAD_NO_CACHE
    //         settings.pluginState = WebSettings.PluginState.OFF
    //         settings.displayZoomControls = false
    //         settings.allowFileAccess = true
    //         settings.allowContentAccess = true
    //         settings.savePassword = false
    //         settings.saveFormData = false
    //         settings.javaScriptEnabled = true
    //         settings.domStorageEnabled = true
    //         settings.setSupportMultipleWindows(true)
    //         settings.mediaPlaybackRequiresUserGesture = false
    //         settings.allowFileAccessFromFileURLs = true
    //         settings.allowUniversalAccessFromFileURLs = true
    //         settings.javaScriptCanOpenWindowsAutomatically = false
    //         // settings.loadsImagesAutomatically = false
    //         // settings.blockNetworkImage = true
    //         // settings.blockNetworkLoads = false
    //         settings.databaseEnabled = true
    //
    //         setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
    //     }
    // }

    private fun installPlugin() {
        try {
            val pluginJson = readStream(assets.open("plugin.json")).toString(Charsets.UTF_8)
            pluginManager.installPluginJson(pluginJson)
        } catch (e: Exception) {
            e.printStackTrace()
            mainHandler.obtainMessage(0, e.message).sendToTarget()
        }
    }

    private fun uninstallPlugin() {
        val pluginId = etUninstallPluginId.text.toString()
        sharedPreferences.edit {
            putString(SHAR_PER_KEY_UNINSTALL_PLUGIN_ID, pluginId)
        }
        try {
            pluginManager.uninstallPlugin(pluginId)
        } catch (e: Exception) {
            e.printStackTrace()
            mainHandler.obtainMessage(0, e.message).sendToTarget()
        }
    }

    private fun request() {
        val pluginId = etReqPluginId.text.toString()
        val datasetId = etReqDatasetId.text.toString()
        // wvTest.loadUrl("https://www.kkcechi.com")

        sharedPreferences.edit {
            putString(SHAR_PER_KEY_REQ_PLUGIN_ID, pluginId)
            putString(SHAR_PER_KEY_REQ_DATASET_ID, datasetId)
        }

        backgroundExecutor.execute {
            Log.d(TAG, "request: dataset $pluginId $datasetId")
            try {
                val result = pluginManager.requestDataset(
                    pluginId,
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

    private fun getInstalledPlugins() {
        val plugins = pluginManager.getInstalledPlugins()
        Log.d(TAG, "getInstalledPlugins: $plugins")
        if (plugins == null) {
            mainHandler.obtainMessage(0, "[]").sendToTarget()
            return
        }

        val result = StringBuilder()
        for (plugin in plugins) {
            result.append(plugin.id + " ->\n")
            for (dataset in plugin.datasets) {
                result.append("    " + dataset.id + "\n")
            }
        }

        mainHandler.obtainMessage(0, result.toString()).sendToTarget()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val SHAR_PER_KEY_UNINSTALL_PLUGIN_ID = "uninstall_plugin_id"
        private const val SHAR_PER_KEY_REQ_PLUGIN_ID = "req_plugin_id"
        private const val SHAR_PER_KEY_REQ_DATASET_ID = "req_dataset_id"

        init {
            System.loadLibrary("spiderx_runtime")
        }
    }

}