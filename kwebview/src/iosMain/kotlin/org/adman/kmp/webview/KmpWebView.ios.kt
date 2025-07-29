package org.adman.kmp.webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKNavigationTypeLinkActivated
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun KmpWebView(
    modifier: Modifier?,
    url: Url?,
    htmlContent: HtmlContent?,
    enableJavaScript: Boolean,
    allowCookies: Boolean,
    enableDomStorage: Boolean,
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
) {
    val config = WKWebViewConfiguration().apply {
        allowsInlineMediaPlayback = true
        allowsAirPlayForMediaPlayback = true
        allowsPictureInPictureMediaPlayback = true
    }

    val webView = remember { WKWebView(CGRectZero.readValue(), config) }

    // Enable java script content
    webView.configuration.defaultWebpagePreferences.allowsContentJavaScript = enableJavaScript

    // Define the WKNavigationDelegate
    if (onUrlClicked != null) {
        val navigationDelegate = rememberWebViewDelegate(onUrlClicked, onLoadingFinished = {isLoading(false)})
        webView.navigationDelegate = navigationDelegate
    }

    UIKitView(
        factory = {
            val container = UIView()

            webView.translatesAutoresizingMaskIntoConstraints = false
            container.addSubview(webView)

            NSLayoutConstraint.activateConstraints(
                listOf(
                    webView.topAnchor.constraintEqualToAnchor(container.topAnchor),
                    webView.bottomAnchor.constraintEqualToAnchor(container.bottomAnchor),
                    webView.leadingAnchor.constraintEqualToAnchor(container.leadingAnchor),
                    webView.trailingAnchor.constraintEqualToAnchor(container.trailingAnchor)
                )
            )

            when {
                htmlContent != null -> webView.loadHTMLString(htmlContent, baseURL = null)
                url != null -> webView.loadRequest(NSURLRequest(NSURL(string = url)))
                else -> println("⚠️ No URL or HTML content provided")
            }

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
private fun rememberWebViewDelegate(onUrlClicked: (String) -> Unit,onLoadingFinished:()->Unit): WKNavigationDelegateProtocol {
    return object : NSObject(), WKNavigationDelegateProtocol {
        override fun webView(
            webView: WKWebView,
            decidePolicyForNavigationAction: WKNavigationAction,
            decisionHandler: (WKNavigationActionPolicy) -> Unit
        ) {
            onLoadingFinished()
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
