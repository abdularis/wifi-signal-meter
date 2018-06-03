package com.aar.app.wifinetanalyzer.di

import android.app.Application
import android.content.Context
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.data.DbHelper
import com.aar.app.wifinetanalyzer.data.VendorFinder
import com.aar.app.wifinetanalyzer.ouilookup.OuiLookupViewModel
import com.aar.app.wifinetanalyzer.ping.PingViewModel
import com.aar.app.wifinetanalyzer.scanner.NetworkScanner
import com.aar.app.wifinetanalyzer.scanner.NetworkScannerViewModel
import com.aar.app.wifinetanalyzer.settings.SettingsProvider
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
                               signalTimeGraphViewModel: SignalTimeGraphViewModel,
                               ouiLookupViewModel: OuiLookupViewModel,
                               pingViewModel: PingViewModel,
                               networkScannerViewModel: NetworkScannerViewModel) : ViewModelFactory {
        return ViewModelFactory(
                signalMeterViewModel,
                wifiListViewModel,
                signalTimeGraphViewModel,
                ouiLookupViewModel,
                pingViewModel,
                networkScannerViewModel)
    }

    @Provides
    @Singleton
    fun provideWifiSignalProvider(ctx: Context): WifiSignalProvider {
        return WifiSignalProvider(ctx, VendorFinder(DbHelper(ctx)))
    }

    @Provides
    @Singleton
    fun provideSignalMeterViewModel(wifiSignalProvider: WifiSignalProvider, settingsProvider: SettingsProvider) : SignalMeterViewModel {
        return SignalMeterViewModel(wifiSignalProvider, settingsProvider)
    }

    @Provides
    @Singleton
    fun provideWifiListViewModel(wifiSignalProvider: WifiSignalProvider): WifiListViewModel {
        return WifiListViewModel(wifiSignalProvider)
    }

    @Provides
    @Singleton
    fun provideSignalTimeGraphViewModel(wifiSignalProvider: WifiSignalProvider, settingsProvider: SettingsProvider): SignalTimeGraphViewModel {
        return SignalTimeGraphViewModel(wifiSignalProvider, settingsProvider)
    }

    @Provides
    @Singleton
    fun provideOuiLookupViewModel(ctx: Context): OuiLookupViewModel {
        return OuiLookupViewModel(VendorFinder(DbHelper(ctx)))
    }

    @Provides
    @Singleton
    fun providePingViewModel(ctx: Context): PingViewModel {
        return PingViewModel(VendorFinder(DbHelper(ctx)))
    }

    @Provides
    @Singleton
    fun provideNetworkScannerViewModel(ctx: Context, settingsProvider: SettingsProvider): NetworkScannerViewModel {
        return NetworkScannerViewModel(NetworkScanner(ctx, VendorFinder(DbHelper(ctx))), settingsProvider)
    }

    @Provides
    @Singleton
    fun provideSettingsProvider(ctx: Context): SettingsProvider {
        return SettingsProvider(ctx)
    }
}