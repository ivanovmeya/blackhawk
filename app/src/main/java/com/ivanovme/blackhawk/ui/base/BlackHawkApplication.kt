package com.ivanovme.blackhawk.ui.base

import android.app.Application
import com.ivanovme.blackhawk.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BlackHawkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BlackHawkApplication)
            modules(mainModule)
        }
    }
}