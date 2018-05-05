package com.github.abdularis.wifisignalmeter.di

import android.app.Application
import android.content.Context
import com.github.abdularis.wifisignalmeter.ViewModelFactor
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
    fun provideViewModelFactor(signalMeterViewModel: SignalMeterViewModel, wifiListViewModel: WifiListViewModel) : ViewModelFactor {
        return ViewModelFactor(signalMeterViewModel, wifiListViewModel)
    }

    @Provides
    @Singleton
    fun provideSignalMeterViewModel(ctx : Context) : SignalMeterViewModel {
        return SignalMeterViewModel(WifiSignalProvider(ctx))
    }

    @Provides
    @Singleton
    fun provideWifiListViewModel(ctx: Context): WifiListViewModel {
        return WifiListViewModel(WifiSignalProvider(ctx))
    }
}