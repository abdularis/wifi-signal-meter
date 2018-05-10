package com.aar.app.wifinetanalyzer.di

import com.aar.app.wifinetanalyzer.signalmeter.SignalMeterFragment
import com.aar.app.wifinetanalyzer.timegraph.SignalTimeGraphFragment
import com.aar.app.wifinetanalyzer.wifiselector.WifiSelectorActivity
import com.aar.app.wifinetanalyzer.wifilist.WifiListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    fun inject(client: SignalMeterFragment)
    fun inject(client: WifiListFragment)
    fun inject(client: WifiSelectorActivity)
    fun inject(client: SignalTimeGraphFragment)
}