package org.adman.kmp.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias Url = String
typealias HtmlContent = String

data class Cookies(
    val name: String,
    val value: String,
    val domain: String,
    val path: String = "/",
    val expires: String? = null,
    val maxAge: Long? = null,
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val sameSite: String? = null
)

@Composable
internal expect fun KmpWebView(
    modifier: Modifier?,
    url: Url?,
    htmlContent: HtmlContent?,
    enableJavaScript: Boolean = false,
    allowCookies: Boolean = false,
    injectCookies: List<Cookies> = emptyList(),
    enableDomStorage: Boolean = false,
    isLoading: (isLoading: Boolean) -> Unit,
    onUrlClicked: ((url: String) -> Unit)?
)




fun List<Cookies>.injectCookieStrings(onSetCookies:(String)->Unit) {
    return this.forEach { cookie ->
        val builder = StringBuilder()
        builder.append("${cookie.name}=${cookie.value}")

        if (cookie.domain.isNotEmpty()) {
            builder.append("; Domain=${cookie.domain}")
        }
        if (cookie.path.isNotEmpty()) {
            builder.append("; Path=${cookie.path}")
        }
        if (cookie.expires != null) {
            builder.append("; Expires=${cookie.expires}")
        } else if (cookie.maxAge != null) {
            builder.append("; Max-Age=${cookie.maxAge}")
        }
        if (cookie.secure) {
            builder.append("; Secure")
        }
        if (cookie.httpOnly) {
            builder.append("; HttpOnly")
        }
        if (cookie.sameSite != null && cookie.sameSite.isNotEmpty()) {
            builder.append("; SameSite=${cookie.sameSite}")
        }
        onSetCookies(builder.toString())
    }
}
