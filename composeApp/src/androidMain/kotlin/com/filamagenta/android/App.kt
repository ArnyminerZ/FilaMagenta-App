package com.filamagenta.android

import accounts.AccountManager
import android.app.Application
import android.os.Handler
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        AccountManager.startWatching(Handler(mainLooper))
    }

    override fun onTerminate() {
        super.onTerminate()

        AccountManager.stopWatching()
    }
}
