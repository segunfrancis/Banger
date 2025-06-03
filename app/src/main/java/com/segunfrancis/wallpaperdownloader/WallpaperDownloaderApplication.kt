package com.segunfrancis.wallpaperdownloader

import android.app.Application
import com.segunfrancis.details.di.detailsModule
import com.segunfrancis.home.di.homeModule
import com.segunfrancis.local.localModule
import com.segunfrancis.remote.remoteModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WallpaperDownloaderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WallpaperDownloaderApplication)
            modules(remoteModule, localModule, homeModule, detailsModule)
        }
    }
}
