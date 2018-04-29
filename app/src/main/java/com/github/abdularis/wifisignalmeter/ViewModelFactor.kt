package com.github.abdularis.wifisignalmeter

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterViewModel
import javax.inject.Inject

class ViewModelFactor @Inject constructor(
        private val signalMeterViewModel: SignalMeterViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignalMeterViewModel::class.java))
            return signalMeterViewModel as T
        throw IllegalArgumentException("Unknown view model type")
    }
}