package com.aar.app.wifinetanalyzer.di

import android.app.Application
import android.content.Context
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.data.DbHelper
import com.aar.app.wifinetanalyzer.data.VendorFinder
import com.aar.app.wifinetanalyzer.signalmeter.SignalMeterViewModel
import com.aar.app.wifinetanalyzer.signalmeter.WifiSignalProvider
import com.aar.app.wifinetanalyzer.timegraph.SignalTimeGraphViewModel
import com.aar.app.wifinetanalyzer.wifilist.WifiListViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application : Application) {
    @Provides
    @Singleton
    fun provideContext() : Context = application

    @Provides
    @Singleton
    fun provideViewModelFactor(signalMeterViewModel: SignalMeterViewModel,
                               wifiListViewModel: WifiListViewModel,
                               signalTimeGraphViewModel: SignalTimeGraphViewModel) : ViewModelFactory {
        return ViewModelFactory(signalMeterViewModel, wifiListViewModel, signalTimeGraphViewModel)
    }

    @Provides
    @Singleton
    fun provideWifiSignalProvider(ctx: Context): WifiSignalProvider {
        return WifiSignalProvider(ctx, VendorFinder(DbHelper(ctx)))
    }

    @Provides
    @Singleton
    fun provideSignalMeterViewModel(wifiSignalProvider: WifiSignalProvider) : SignalMeterViewModel {
        return SignalMeterViewModel(wifiSignalProvider)
    }

    @Provides
    @Singleton
    fun provideWifiListViewModel(wifiSignalProvider: WifiSignalProvider): WifiListViewModel {
        return WifiListViewModel(wifiSignalProvider)
    }

    @Provides
    @Singleton
    fun provideSignalTimeGraphViewModel(wifiSignalProvider: WifiSignalProvider): SignalTimeGraphViewModel {
        return SignalTimeGraphViewModel(wifiSignalProvider)
    }
}