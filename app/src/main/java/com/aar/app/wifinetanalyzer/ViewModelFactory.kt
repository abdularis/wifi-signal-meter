package com.aar.app.wifinetanalyzer

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aar.app.wifinetanalyzer.signalmeter.SignalMeterViewModel
import com.aar.app.wifinetanalyzer.timegraph.SignalTimeGraphViewModel
import com.aar.app.wifinetanalyzer.wifilist.WifiListViewModel
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
        private val signalMeterViewModel: SignalMeterViewModel,
        private val wifiListViewModel: WifiListViewModel,
        private val signalTimeGraphViewModel: SignalTimeGraphViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignalMeterViewModel::class.java))
            return signalMeterViewModel as T
        else if (modelClass.isAssignableFrom(WifiListViewModel::class.java))
            return wifiListViewModel as T
        else if (modelClass.isAssignableFrom(SignalTimeGraphViewModel::class.java))
            return signalTimeGraphViewModel as T
        throw IllegalArgumentException("Unknown view model")
    }
}