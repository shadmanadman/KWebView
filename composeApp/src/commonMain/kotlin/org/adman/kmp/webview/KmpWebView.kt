package org.adman.kmp.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias Url = String
typealias HtmlContent = String
@Composable
expect fun KmpWebView(modifier: Modifier?, url: Url?,htmlContent: HtmlContent?, isLoading:(isLoading:Boolean)->Unit, onUrlClicked:((url:String)->Unit)?)