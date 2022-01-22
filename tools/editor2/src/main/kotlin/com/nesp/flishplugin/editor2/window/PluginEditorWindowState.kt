package com.nesp.flishplugin.editor2.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.WindowState
import com.nesp.flishplugin.editor2.app.PluginEditorApplicationState

class PluginEditorWindowState(
    private val application: PluginEditorApplicationState
) {
    val settings: Settings get() = application.settings

    val window = WindowState()


    var isChanged by mutableStateOf(false)
        private set
}