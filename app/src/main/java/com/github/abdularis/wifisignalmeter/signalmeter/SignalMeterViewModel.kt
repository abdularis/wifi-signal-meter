package com.github.abdularis.wifisignalmeter.signalmeter

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.model.WifiAp
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignalMeterViewModel @Inject constructor(private val wifiSignalProvider: WifiSignalProvider) : ViewModel() {

    val wifiList: MutableLiveData<List<WifiAp>> = MutableLiveData()
    val wifiUpdates: MutableLiveData<WifiAp> = MutableLiveData()
    var disposable: Disposable? = null
    var bssidFilter: String = ""
        set(value) {
            if (value != field) {
                field = value
                disposable?.dispose()
                startWifiUpdates()
            }
        }

    fun startWifiUpdates() {
        disposable?.dispose()
        disposable = wifiSignalProvider.wifiSignalUpdate(2000)
                .flatMap {
                    wifiList.postValue(it)
                    Flowable.fromIterable(it)
                            .filter {
                                if (bssidFilter.isEmpty())
                                    it.isConnected else
                                    it.bssid == bssidFilter
                            }
                            .map { Optional.of(it) }
                            .defaultIfEmpty(Optional.empty())
                }
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