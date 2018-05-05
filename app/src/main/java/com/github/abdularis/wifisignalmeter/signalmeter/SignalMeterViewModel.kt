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
    val wifiUpdates: MutableLiveData<WifiAccessPoint> = MutableLiveData()
    var bssidFilter: String = ""
        set(value) {
            if (value != field) {
                field = value
                stopSignalUpdates()
                startWifiUpdates()
            }
        }

    fun startWifiUpdates() {
        val flowable: Flowable<Optional<WifiAccessPoint>> = if (bssidFilter.isEmpty()) {
            wifiSignalProvider.connectedWifiSignalUpdate(2000)
        } else {
            wifiSignalProvider.wifiSignalUpdate(2000, bssidFilter)
        }

        disposable?.dispose()
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
    }

}