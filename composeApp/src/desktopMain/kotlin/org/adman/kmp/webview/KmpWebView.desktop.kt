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
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
) {
    val jPanel: JPanel = remember { JPanel() }
    val jfxPanel = JFXPanel()
    SwingPanel(
        factory = {
            jfxPanel.apply { buildWebView(url,htmlContent,isLoading, onUrlClicked) }
            jPanel.add(jfxPanel)
        },
        modifier = modifier ?: Modifier.fillMaxSize(),
    )
    DisposableEffect(true) { onDispose { jPanel.remove(jfxPanel) } }
}


private fun JFXPanel.buildWebView(
    url: String?,
    htmlContent: HtmlContent?,
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
){
    initJavaFX()
    Platform.runLater {
        val webView = WebView()
        val webEngine = webView.engine

        webEngine.userAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"

        //webEngine.isJavaScriptEnabled = true
        scene = Scene(webView)
        htmlContent.let {
            webEngine.loadContent(it)
        }
        url.let {
            webEngine.load(it)
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