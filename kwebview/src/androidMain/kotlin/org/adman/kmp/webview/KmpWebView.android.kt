package org.adman.kmp.webview

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal actual fun KmpWebView(
    modifier: Modifier?,
    url: Url?,
    htmlContent: HtmlContent?,
    enableJavaScript: Boolean,
    allowCookies: Boolean,
    injectCookies: List<Cookies>,
    enableDomStorage: Boolean,
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
                        domStorageEnabled = enableDomStorage
                    }

                    // Enable/Inject cookies
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(allowCookies)
                    // TODO needs optimization
                    injectCookies.injectCookieStrings{ injectCookie->
                        cookieManager.setCookie(url?:"",injectCookie)
                    }
                    cookieManager.flush()

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

                                view?.evaluateJavascript("document.cookie"
                                ) { cookieString ->
                                    Log.d("WebViewCookieTest", "Cookies retrieved: $cookieString")
                                }
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
                when {
                    htmlContent != null -> webView.loadDataWithBaseURL(
                        "",
                        /* data = */ htmlContent, /* mimeType = */
                        "text/html", /* encoding = */
                        "UTF-8",
                        ""
                    )

                    url != null -> webView.loadUrl(url)
                    else -> println("⚠️ No URL or HTML content provided")
                }
            })
    }
}