package org.adman.kmp.webview

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { KWebView(url = "https://www.google.com/") }