package com.github.abdularis.wifisignalmeter.di

import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterFragment
import com.github.abdularis.wifisignalmeter.signalmeter.aplistdialog.WifiApListDialogFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(client : SignalMeterFragment)
    fun inject(client: WifiApListDialogFragment)
}
