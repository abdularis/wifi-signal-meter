package com.github.abdularis.wifisignalmeter.signalmeter

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignalMeterViewModel @Inject constructor(private val wifiSignalProvider: WifiSignalProvider) : ViewModel() {

    private var disposable: Disposable? = null
    private var ssid = ""
    private var bssid = ""
    val wifiUpdates: MutableLiveData<WifiAccessPoint> = MutableLiveData()

    fun filter(ssid: String, bssid: String) {
        this.ssid = ssid
        this.bssid = bssid
        if (disposable != null) {
            stopSignalUpdates()
            startWifiUpdates()
        }
    }

    fun startWifiUpdates() {
        if (disposable != null) return

        val flowable: Flowable<Optional<WifiAccessPoint>> = if (bssid.isEmpty()) {
            wifiSignalProvider.connectedWifiSignalUpdate(2000)
        } else {
            wifiSignalProvider.wifiSignalUpdate(2000, ssid, bssid)
        }

        disposable = flowable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    wifiUpdates.value = it.value
                }, {
                    wifiUpdates.value = null
                })
    }

    fun stopSignalUpdates() {
        disposable?.dispose()
        disposable = null
    }

}