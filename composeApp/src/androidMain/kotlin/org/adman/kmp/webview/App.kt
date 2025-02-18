package org.adman.kmp.webview

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.setUp(applicationContext)
    }
}