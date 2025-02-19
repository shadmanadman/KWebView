package org.adman.kmp.webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIView
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigationTypeLinkActivated
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject

@Composable
actual fun KmpWebView(
    modifier: Modifier?,
    url: Url?,
    htmlContent: HtmlContent?,
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
) {
    val webView = remember { WKWebView() }

    // Define the WKNavigationDelegate
    if (onUrlClicked != null) {
        val navigationDelegate = rememberWebViewDelegate(onUrlClicked)
        webView.navigationDelegate = navigationDelegate
    }
    UIKitView(
        factory = {
            val container = UIView()
            webView.apply {
                WKWebViewConfiguration().apply {
                    allowsInlineMediaPlayback = true
                    allowsAirPlayForMediaPlayback = true
                    allowsPictureInPictureMediaPlayback = true
                }
                if (htmlContent != null)
                    loadHTMLString(htmlContent, baseURL = null)
                else
                    loadRequest(NSURLRequest(NSURL(string = url ?: "")))
            }
            container.addSubview(webView)
            container
        },
        modifier = modifier ?: Modifier.fillMaxSize(),
        properties = UIKitInteropProperties(
            isInteractive = true,
            isNativeAccessibilityEnabled = true
        )
    )
}

@Composable
private fun rememberWebViewDelegate(onUrlClicked: (String) -> Unit): WKNavigationDelegateProtocol {
    return object : NSObject(), WKNavigationDelegateProtocol {
        override fun webView(
            webView: WKWebView,
            decidePolicyForNavigationAction: WKNavigationAction,
            decisionHandler: (WKNavigationActionPolicy) -> Unit
        ) {
            val navigationType = decidePolicyForNavigationAction.navigationType
            val request = decidePolicyForNavigationAction.request

            when (navigationType) {
                WKNavigationTypeLinkActivated -> {
                    // Handle link clicks
                    if (decidePolicyForNavigationAction.targetFrame == null) {
                        // Load the link in the same WKWebView
                        webView.loadRequest(request)
                    }
                    onUrlClicked(request.URL?.absoluteString ?: "")
                    println(request.URL?.absoluteString ?: "")
                    decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
                }

                else -> decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
            }
        }
    }
}
