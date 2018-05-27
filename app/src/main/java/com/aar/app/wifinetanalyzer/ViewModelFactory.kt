package com.aar.app.wifinetanalyzer

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aar.app.wifinetanalyzer.ouilookup.OuiLookupViewModel
import com.aar.app.wifinetanalyzer.ping.PingViewModel
import com.aar.app.wifinetanalyzer.scanner.NetworkScannerViewModel
import com.aar.app.wifinetanalyzer.signalmeter.SignalMeterViewModel
import com.aar.app.wifinetanalyzer.timegraph.SignalTimeGraphViewModel
import com.aar.app.wifinetanalyzer.wifilist.WifiListViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
        private val signalMeterViewModel: SignalMeterViewModel,
        private val wifiListViewModel: WifiListViewModel,
        private val signalTimeGraphViewModel: SignalTimeGraphViewModel,
        private val ouiLookupViewModel: OuiLookupViewModel,
        private val pingViewModel: PingViewModel,
        private val networkScannerViewModel: NetworkScannerViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignalMeterViewModel::class.java) -> signalMeterViewModel as T
            modelClass.isAssignableFrom(WifiListViewModel::class.java) -> wifiListViewModel as T
            modelClass.isAssignableFrom(SignalTimeGraphViewModel::class.java) -> signalTimeGraphViewModel as T
            modelClass.isAssignableFrom(OuiLookupViewModel::class.java) -> ouiLookupViewModel as T
            modelClass.isAssignableFrom(PingViewModel::class.java) -> pingViewModel as T
            modelClass.isAssignableFrom(NetworkScannerViewModel::class.java) -> networkScannerViewModel as T
            else -> throw IllegalArgumentException("Unknown view model")
        }
    }
}