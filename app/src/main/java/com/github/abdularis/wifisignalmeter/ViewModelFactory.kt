package com.github.abdularis.wifisignalmeter

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterViewModel
import com.github.abdularis.wifisignalmeter.timegraph.SignalTimeGraphViewModel
import com.github.abdularis.wifisignalmeter.wifilist.WifiListViewModel
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