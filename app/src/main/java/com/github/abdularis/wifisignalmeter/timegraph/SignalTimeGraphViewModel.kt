package com.github.abdularis.wifisignalmeter.timegraph

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.model.SignalshotBuffer
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.signalmeter.WifiSignalProvider
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignalTimeGraphViewModel @Inject constructor(private val wifiSignalProvider: WifiSignalProvider): ViewModel() {

    private var disposable: Disposable? = null
    private var ssid = ""
    private var bssid = ""
    private val _signalshotBuffer: SignalshotBuffer = SignalshotBuffer()
    val signalshotBuffer: MutableLiveData<SignalshotBuffer> = MutableLiveData()

    fun filter(ssid: String, bssid: String) {
        this.ssid = ssid
        this.bssid = bssid
        if (disposable != null) {
            stopUpdates()
            startUpdates()
        }

        _signalshotBuffer.clear()
    }

    fun startUpdates() {
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
                    it?.let {
                        _signalshotBuffer.add(it.value)
                        signalshotBuffer.value = _signalshotBuffer
                    }
                }, {

                })
    }

    fun stopUpdates() {
        disposable?.dispose()
        disposable = null
    }
}