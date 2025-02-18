package org.adman.kmp.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias UrlOrHtmlContent = String
@Composable
expect fun KmpWebView(modifier: Modifier, urlOrHtmlContent: UrlOrHtmlContent, isLoading:(isLoading:Boolean)->Unit, onUrlClicked:(url:String)->Unit)