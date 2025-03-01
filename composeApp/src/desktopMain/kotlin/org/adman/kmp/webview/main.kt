package org.adman.kmp.webview

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kmp-Player",
    ) {
        KmpWebViewScreen(url = "https://www.google.com/")
    }
}