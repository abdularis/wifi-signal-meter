package com.github.abdularis.wifisignalmeter.di

import android.app.Application
import android.content.Context
import com.github.abdularis.wifisignalmeter.ViewModelFactor
import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterViewModel
import com.github.abdularis.wifisignalmeter.signalmeter.WifiSignalProvider
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
    fun provideViewModelFactor(signalMeterViewModel: SignalMeterViewModel) : ViewModelFactor {
        return ViewModelFactor(signalMeterViewModel)
    }

    @Provides
    @Singleton
    fun provideSignalMeterViewModel(ctx : Context) : SignalMeterViewModel {
        return SignalMeterViewModel(WifiSignalProvider(ctx))
    }
}