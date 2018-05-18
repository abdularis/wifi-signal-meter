package com.aar.app.wifinetanalyzer.wifilist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aar.app.wifinetanalyzer.filter.AbstractWifiFilter
import com.aar.app.wifinetanalyzer.filter.CompositeFilter
import com.aar.app.wifinetanalyzer.filter.NoFilter
import com.aar.app.wifinetanalyzer.signalmeter.WifiSignalProvider
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WifiListViewModel(private val wifiSignalProvider: WifiSignalProvider): ViewModel() {

    private var disposable: Disposable? = null
    private var filter: AbstractWifiFilter = NoFilter()
        set(value) {
            field = value
            if (disposable != null) {
                stopListUpdate()
                startListUpdate()
            }
            onFilterChanged.value = value
        }

    val wifiAccessPointList: MutableLiveData<WifiAccessPointList> = MutableLiveData()
    val onFilterChanged: MutableLiveData<AbstractWifiFilter> = MutableLiveData()

    init {
        onFilterChanged.value = filter
    }

    fun filterList(filter: AbstractWifiFilter) {
        this.filter = filter
    }

    fun clearFilter() {
        filter = NoFilter()
    }

    fun isFilterOn(): Boolean {
        if (filter is NoFilter) {
            return false
        } else if (filter is CompositeFilter) {
            if ((filter as CompositeFilter).isEmpty()) {
                return false
            }
        }
        return true
    }

    fun startListUpdate() {
        disposable = wifiSignalProvider.wifiSignalUpdate(2000)
                .flatMap {
                    Flowable.fromIterable(it)
                            .filter(filter)
                            .toList()
                            .toFlowable()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    wifiAccessPointList.value = WifiAccessPointList(it)
                }, {
                    wifiAccessPointList.value = null
                })
    }

    fun stopListUpdate() {
        disposable?.dispose()
        disposable = null
    }

}