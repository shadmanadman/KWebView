package org.adman.kmp.webview

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal actual fun KmpWebView(
    modifier: Modifier?,
    url: Url?,
    htmlContent: HtmlContent?,
    enableJavaScript: Boolean,
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
) {
    Box(
        modifier = modifier ?: Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AndroidView(
            modifier = modifier ?: Modifier.fillMaxSize(),
            factory = {
                WebView(AppContext.get()).apply {
                    scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
                    setBackgroundColor(Color.TRANSPARENT)

                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )

                    // Enable horizontal scrolling and JavaScript
                    settings.apply {
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                        javaScriptEnabled = enableJavaScript
                    }
                    webViewClient =
                        object : WebViewClient() {

                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                isLoading(true)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                view?.scrollTo(view.contentHeight, 0)
                                isLoading(false)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return if (request?.url.toString().contains("jpg") ||
                                    request?.url?.toString()?.contains("png")!! ||
                                    request.url?.toString()?.contains("attachment_id")!!
                                ) {
                                    true
                                } else {
                                    if (onUrlClicked != null)
                                        onUrlClicked(request.url.toString())
                                    true
                                }
                            }
                        }

                }
            },
            update = { webView ->
                if (url == null)
                    htmlContent?.let {
                        webView.loadDataWithBaseURL(
                            "",
                            /* data = */ it, /* mimeType = */
                            "text/html", /* encoding = */
                            "UTF-8",
                            ""
                        )
                    }
                else
                    url.let {
                        webView.loadUrl(it)
                    }
            })
    }
}