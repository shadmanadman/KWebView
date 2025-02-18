package org.adman.kmp.webview

import androidx.compose.runtime.Composable

typealias UrlOrHtmlContent = String
@Composable
expect fun KmpWebView(urlOrHtmlContent: UrlOrHtmlContent, isLoading:(isLoading:Boolean)->Unit, onUrlClicked:(url:String)->Unit)