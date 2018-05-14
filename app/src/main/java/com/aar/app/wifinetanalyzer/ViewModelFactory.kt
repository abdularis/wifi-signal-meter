package com.aar.app.wifinetanalyzer

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aar.app.wifinetanalyzer.ouilookup.OuiLookupViewModel
import com.aar.app.wifinetanalyzer.signalmeter.SignalMeterViewModel
import com.aar.app.wifinetanalyzer.timegraph.SignalTimeGraphViewModel
import com.aar.app.wifinetanalyzer.wifilist.WifiListViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
        private val signalMeterViewModel: SignalMeterViewModel,
        private val wifiListViewModel: WifiListViewModel,
        private val signalTimeGraphViewModel: SignalTimeGraphViewModel,
        private val ouiLookupViewModel: OuiLookupViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignalMeterViewModel::class.java) -> signalMeterViewModel as T
            modelClass.isAssignableFrom(WifiListViewModel::class.java) -> wifiListViewModel as T
            modelClass.isAssignableFrom(SignalTimeGraphViewModel::class.java) -> signalTimeGraphViewModel as T
            modelClass.isAssignableFrom(OuiLookupViewModel::class.java) -> ouiLookupViewModel as T
            else -> throw IllegalArgumentException("Unknown view model")
        }
    }
}