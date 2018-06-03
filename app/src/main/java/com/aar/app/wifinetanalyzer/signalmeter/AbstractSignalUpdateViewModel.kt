package com.aar.app.wifinetanalyzer.signalmeter

import android.arch.lifecycle.ViewModel
import com.aar.app.wifinetanalyzer.common.Optional
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import com.aar.app.wifinetanalyzer.settings.SettingsProvider
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class AbstractSignalUpdateViewModel(
        protected val wifiSignalProvider: WifiSignalProvider,
        protected val settingsProvider: SettingsProvider): ViewModel() {

    private var disposable: Disposable? = null
    protected var ssid = ""
    protected var bssid = ""

    val scanInterval get() = (settingsProvider.scanInterval * 1000).toLong()

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
            wifiSignalProvider.connectedWifiSignalUpdate(scanInterval)
        } else {
            wifiSignalProvider.wifiSignalUpdate(scanInterval, ssid, bssid)
        }

        return flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}