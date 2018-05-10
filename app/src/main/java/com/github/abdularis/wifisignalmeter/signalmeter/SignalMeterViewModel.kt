package com.github.abdularis.wifisignalmeter.signalmeter

import android.arch.lifecycle.MutableLiveData
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class SignalMeterViewModel @Inject constructor(wifiSignalProvider: WifiSignalProvider)
    : AbstractSignalUpdateViewModel(wifiSignalProvider) {

    val wifiUpdates: MutableLiveData<WifiAccessPoint> = MutableLiveData()

    override fun onSignalUpdateSubscribe(signalUpdateFloawable: Flowable<Optional<WifiAccessPoint>>): Disposable {
        return signalUpdateFloawable
                .subscribe({
                    wifiUpdates.value = it.value
                }, {
                    wifiUpdates.value = null
                })
    }

}