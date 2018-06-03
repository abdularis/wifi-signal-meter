package com.aar.app.wifinetanalyzer.timegraph

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aar.app.wifinetanalyzer.common.Optional
import com.aar.app.wifinetanalyzer.model.SignalshotBuffer
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import com.aar.app.wifinetanalyzer.settings.SettingsProvider
import com.aar.app.wifinetanalyzer.signalmeter.AbstractSignalUpdateViewModel
import com.aar.app.wifinetanalyzer.signalmeter.WifiSignalProvider
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignalTimeGraphViewModel @Inject constructor(wifiSignalProvider: WifiSignalProvider, settingsProvider: SettingsProvider)
    : AbstractSignalUpdateViewModel(wifiSignalProvider, settingsProvider) {

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
