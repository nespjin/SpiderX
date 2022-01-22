package com.nesp.flishplugin.editor2.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.TrayState
import com.nesp.flishplugin.editor2.settings.Settings


@Composable
fun rememberApplicationState(){

}

class PluginEditorApplicationState {
    val settings = Settings()
    val tray = TrayState()

    private val _windows = mutableListOf<Window>()
}