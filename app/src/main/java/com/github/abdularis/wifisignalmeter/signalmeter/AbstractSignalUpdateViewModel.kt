package com.github.abdularis.wifisignalmeter.signalmeter

import android.arch.lifecycle.ViewModel
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class AbstractSignalUpdateViewModel(protected val wifiSignalProvider: WifiSignalProvider): ViewModel() {

    private var disposable: Disposable? = null
    protected var ssid = ""
    protected var bssid = ""

    fun startSignalUpdate() {
        if (disposable != null) return
        disposable = onSignalUpdateSubscribe(createSignalUpdateFlowable())
    }

    fun stopSignalUpdate() {
        disposable?.dispose()
        disposable = null
    }

    open fun filter(ssid: String, bssid: String) {
        if (this.ssid == ssid && this.bssid == bssid) return
        this.ssid = ssid
        this.bssid = bssid
        if (disposable != null) {
            stopSignalUpdate()
            startSignalUpdate()
        }
    }

    protected abstract fun onSignalUpdateSubscribe(signalUpdateFloawable: Flowable<Optional<WifiAccessPoint>>): Disposable

    private fun createSignalUpdateFlowable(): Flowable<Optional<WifiAccessPoint>> {
        val flowable: Flowable<Optional<WifiAccessPoint>> = if (bssid.isEmpty()) {
            wifiSignalProvider.connectedWifiSignalUpdate(2000)
        } else {
            wifiSignalProvider.wifiSignalUpdate(2000, ssid, bssid)
        }

        return flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}