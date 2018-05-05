package com.github.abdularis.wifisignalmeter.wifilist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.signalmeter.WifiSignalProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WifiListViewModel(private val wifiSignalProvider: WifiSignalProvider): ViewModel() {

    private var disposable: Disposable? = null
    val wifiList: MutableLiveData<List<WifiAccessPoint>> = MutableLiveData()

    fun startListUpdate() {
        disposable = wifiSignalProvider.wifiSignalUpdate(2000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    wifiList.value = it
                }, {
                    wifiList.value = null
                })
    }

    fun stopListUpdate() {
        disposable?.dispose()
    }

}