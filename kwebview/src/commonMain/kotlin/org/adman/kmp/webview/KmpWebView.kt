package org.adman.kmp.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias Url = String
typealias HtmlContent = String

@Composable
internal expect fun KmpWebView(
    modifier: Modifier?,
    url: Url?,
    htmlContent: HtmlContent?,
    enableJavaScript: Boolean = false,
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
)