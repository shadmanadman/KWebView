package org.adman.kmp.webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URI
import javax.swing.JPanel


fun initJavaFX() {
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.verbose", "true")
}

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
    val jPanel: JPanel = remember { JPanel() }
    val jfxPanel = JFXPanel()
    SwingPanel(
        factory = {
            jfxPanel.apply {
                buildWebView(
                    url,
                    htmlContent,
                    enableJavaScript,
                    allowCookies,
                    injectCookies,
                    isLoading,
                    onUrlClicked
                )
            }
            jPanel.add(jfxPanel)
        },
        modifier = modifier ?: Modifier.fillMaxSize(),
    )
    DisposableEffect(true) { onDispose { jPanel.remove(jfxPanel) } }
}


private fun JFXPanel.buildWebView(
    url: String?,
    htmlContent: HtmlContent?,
    enableJavaScript: Boolean,
    allowCookies: Boolean,
    injectCookies: List<Cookies>,
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
) {
    initJavaFX()
    Platform.runLater {
        val webView = WebView()
        val webEngine = webView.engine

        webEngine.isJavaScriptEnabled = enableJavaScript
        webEngine.userAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"

        // Allow cookies
        val cookieManager = CookieManager()
        when (allowCookies) {
            true -> {
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
                // TODO needs optimization
                injectCookies.injectCookieStrings { injectCookie->
                    val responseHeaders: MutableMap<String?, MutableList<String?>?> =
                        HashMap()
                    responseHeaders.put("Set-Cookie", mutableListOf(injectCookie))
                    cookieManager.put(URI(url?:""),responseHeaders)
                }
            }

            false -> cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_NONE)
        }

        scene = Scene(webView)
        when{
            htmlContent!=null->webEngine.loadContent(htmlContent)
            url!=null->webEngine.load(url)
            else -> println("⚠️ No URL or HTML content provided")
        }

        webEngine.loadWorker.stateProperty().addListener { _, _, newState ->
            isLoading(newState.toString() == "RUNNING")
        }

        webEngine.locationProperty().addListener { _, oldLocation, newLocation ->
            if (oldLocation != newLocation) {
                onUrlClicked?.invoke(newLocation)
            }
        }

    }
}