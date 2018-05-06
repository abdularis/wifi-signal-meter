package com.github.abdularis.wifisignalmeter.di

import android.app.Application
import android.content.Context
import com.github.abdularis.wifisignalmeter.ViewModelFactory
import com.github.abdularis.wifisignalmeter.data.DbHelper
import com.github.abdularis.wifisignalmeter.data.VendorFinder
import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterViewModel
import com.github.abdularis.wifisignalmeter.signalmeter.WifiSignalProvider
import com.github.abdularis.wifisignalmeter.wifilist.WifiListViewModel
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
    fun provideViewModelFactor(signalMeterViewModel: SignalMeterViewModel, wifiListViewModel: WifiListViewModel) : ViewModelFactory {
        return ViewModelFactory(signalMeterViewModel, wifiListViewModel)
    }

    @Provides
    @Singleton
    fun provideWifiSignalProvider(ctx: Context): WifiSignalProvider {
        return WifiSignalProvider(ctx, VendorFinder(DbHelper(ctx)))
    }

    @Provides
    @Singleton
    fun provideSignalMeterViewModel(ctx : Context, wifiSignalProvider: WifiSignalProvider) : SignalMeterViewModel {
        return SignalMeterViewModel(wifiSignalProvider)
    }

    @Provides
    @Singleton
    fun provideWifiListViewModel(ctx: Context, wifiSignalProvider: WifiSignalProvider): WifiListViewModel {
        return WifiListViewModel(wifiSignalProvider)
    }
}