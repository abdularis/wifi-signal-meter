package com.aar.app.wifinetanalyzer.signalmeter

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.aar.app.wifinetanalyzer.common.Optional
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import com.aar.app.wifinetanalyzer.settings.SettingsProvider
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class SignalMeterViewModel @Inject constructor(wifiSignalProvider: WifiSignalProvider, settingsProvider: SettingsProvider)
    : AbstractSignalUpdateViewModel(wifiSignalProvider, settingsProvider) {

    val wifiUpdates: MutableLiveData<WifiAccessPoint> = MutableLiveData()

    override fun onSignalUpdateSubscribe(signalUpdateFloawable: Flowable<Optional<WifiAccessPoint>>): Disposable {
        return signalUpdateFloawable
                .subscribe({
                    Log.d("SignalMeterUpdate", "updated")
                    wifiUpdates.value = it.value
                }, {
                    wifiUpdates.value = null
                })
    }

}