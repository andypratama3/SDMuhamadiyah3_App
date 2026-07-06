package com.sdm3.parent

import android.app.Application
import com.sdm3.parent.core.di.allAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Sdm3Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Sdm3Application)
            modules(allAppModules)
        }
    }
}
