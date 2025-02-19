package org.adman.kmp.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A Composable function that displays a WebView for loading either a URL or HTML content.
 *
 * This function provides a simplified interface for rendering web content within a Compose UI.
 * It handles the display of the content and provides callbacks for loading state and URL interactions.
 *
 * @param modifier Optional [Modifier] to be applied to the WebView.
 * @param url Optional URL to be loaded in the WebView. If provided, `htmlContent` is ignored.
 * @param htmlContent Optional HTML content string to be loaded in the WebView. Ignored if `url` is provided.
 * @param isLoading Callback function invoked with a boolean indicating whether the WebView is currently loading content.
 * @param onUrlClicked Callback function invoked when a URL is clicked within the WebView.
 *                     It receives the clicked URL as a parameter. This allows for custom handling of link clicks.
 *
 * Example Usage:
 * ```kotlin
 * KmpWebViewScreen(
 *     url = "https://www.example.com",
 *     isLoading = { loading ->
 *         println("Is Loading: $loading")
 *         // Update UI based on loading state
 *     },
 *     onUrlClicked = { clickedUrl ->
 *         println("URL Clicked: $clickedUrl")
 *         // Handle URL click, e.g., open in external browser
 *     }
 * )
 *
 * KmpWebViewScreen(
 *      htmlContent = "<!DOCTYPE html><html><body><h1>Hello World!</h1></body></html>",
 *      isLoading = { loading ->
 *          println("Is loading: $loading")
 *      },
 *      onUrlClicked = { clickedUrl ->
 *          println("Url clicked: $clickedUrl")
 *      }
 * )
 * ```
 *
 */
@Composable
@Preview
fun KmpWebViewScreen(
    modifier: Modifier? = null,
    url: String? = null,
    htmlContent: String? = null,
    isLoading: ((isLoading: Boolean) -> Unit)? = null,
    onUrlClicked: ((url: String) -> Unit)? = null
) {
    KmpWebView(
        modifier = modifier,
        url = url ?: "",
        htmlContent = htmlContent ?: "",
        isLoading = isLoading?:{},
        onUrlClicked = onUrlClicked ?: {})
}