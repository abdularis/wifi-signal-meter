package com.github.abdularis.wifisignalmeter.timegraph

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.model.SignalshotBuffer
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.signalmeter.AbstractSignalUpdateViewModel
import com.github.abdularis.wifisignalmeter.signalmeter.WifiSignalProvider
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignalTimeGraphViewModel @Inject constructor(wifiSignalProvider: WifiSignalProvider): AbstractSignalUpdateViewModel(wifiSignalProvider) {

    private val _signalshotBuffer: SignalshotBuffer = SignalshotBuffer()
    val signalshotBuffer: MutableLiveData<SignalshotBuffer> = MutableLiveData()

    override fun filter(ssid: String, bssid: String) {
        super.filter(ssid, bssid)
        _signalshotBuffer.clear()
    }

    override fun onSignalUpdateSubscribe(signalUpdateFloawable: Flowable<Optional<WifiAccessPoint>>): Disposable {
        return signalUpdateFloawable.subscribe({
            it?.let {
                _signalshotBuffer.add(it.value)
                signalshotBuffer.value = _signalshotBuffer
            }
        }, {
            // TODO implement error handling
        })
    }

}
