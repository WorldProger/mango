package com.worldproger.mango.app

import android.app.Application
import com.worldproger.mango.app.core.Constants
import com.worldproger.mango.app.di.appModule
import com.worldproger.mango.data.api.apiModule
import com.worldproger.mango.data.data.dataModule
import com.worldproger.mango.data.storage.storageModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MangoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val apiUrl = Constants.API_URL

        startKoin {
            androidContext(this@MangoApp)
            modules(
                apiModule(apiUrl),
                storageModule(),
                dataModule(),
                appModule(),
            )
        }
    }
}