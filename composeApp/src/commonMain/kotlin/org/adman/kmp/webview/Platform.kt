package org.adman.kmp.webview

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform