package com.aar.app.wifinetanalyzer

import android.app.Application
import com.aar.app.wifinetanalyzer.BuildConfig.ADMOB_APP_ID
import com.aar.app.wifinetanalyzer.di.AppComponent
import com.aar.app.wifinetanalyzer.di.AppModule
import com.aar.app.wifinetanalyzer.di.DaggerAppComponent
import com.google.android.gms.ads.MobileAds

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        val appModule = AppModule(this)
        appComponent = DaggerAppComponent.builder().appModule(appModule).build()

        MobileAds.initialize(this, ADMOB_APP_ID)
    }
}