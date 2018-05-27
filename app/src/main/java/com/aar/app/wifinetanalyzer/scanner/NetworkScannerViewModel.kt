package com.aar.app.wifinetanalyzer.scanner

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aar.app.wifinetanalyzer.scanner.error.EmptyInterfaceAddressException
import com.aar.app.wifinetanalyzer.scanner.error.WifiInterfaceNotFoundException
import com.aar.app.wifinetanalyzer.scanner.error.WifiNotConnectedException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NetworkScannerViewModel(private val networkScanner: NetworkScanner): ViewModel() {

    enum class State {
        SCANNING,
        SCAN_COMPLETED,
        ERR,
        ERR_NOT_CONNECTED,
        ERR_NO_WIFI_FOUND
    }

    private val _scannerResults = ArrayList<ScanResponse>()

    val scannerResults: MutableLiveData<List<ScanResponse>> = MutableLiveData()
    val scannerInfo: MutableLiveData<ScannerInfo> = MutableLiveData()
    val scanProgress: MutableLiveData<Pair<Int, ScanResponse>> = MutableLiveData()
    val state: MutableLiveData<State> = MutableLiveData()

    fun queryScannerInfo() {
        networkScanner.getScannerInfoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    scannerInfo.value = it
                }, {
                    onError(it)
                })
    }

    fun scan() {
        state.value = State.SCANNING
        networkScanner.scanConnectedWifiNetwork()
                .map {
                    scanProgress.postValue(it)
                    it.second
                }
                .filter { it.found }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _scannerResults.add(it)
                    scannerResults.value = _scannerResults
                }, {
                    onError(it)
                }, {
                    state.value = State.SCAN_COMPLETED
                })
    }

    private fun onError(throwable: Throwable) {
        when (throwable) {
            is WifiNotConnectedException -> state.value = State.ERR_NOT_CONNECTED
            is WifiInterfaceNotFoundException -> state.value = State.ERR_NO_WIFI_FOUND
            else -> state.value = State.ERR
        }
    }
}