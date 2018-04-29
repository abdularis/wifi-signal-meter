package com.github.abdularis.wifisignalmeter

import android.app.Application
import com.github.abdularis.wifisignalmeter.di.AppComponent
import com.github.abdularis.wifisignalmeter.di.AppModule
import com.github.abdularis.wifisignalmeter.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        val appModule = AppModule(this)
        appComponent = DaggerAppComponent.builder().appModule(appModule).build()
    }
}