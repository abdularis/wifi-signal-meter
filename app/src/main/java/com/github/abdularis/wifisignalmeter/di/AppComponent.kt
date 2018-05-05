package com.github.abdularis.wifisignalmeter.di

import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterFragment
import com.github.abdularis.wifisignalmeter.wifiselector.WifiSelectorActivity
import com.github.abdularis.wifisignalmeter.wifilist.WifiListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(client: SignalMeterFragment)
    fun inject(client: WifiListFragment)
    fun inject(client: WifiSelectorActivity)
}
