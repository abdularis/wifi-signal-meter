package com.aar.app.wifinetanalyzer.scanner

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NetworkScannerViewModel(private val networkScanner: NetworkScanner): ViewModel() {
    private val _scannerResults = ArrayList<ScanResponse>()
    private var disposable: Disposable? = null

    val scanResults: MutableLiveData<List<ScanResponse>> = MutableLiveData()
    val scanInfo: MutableLiveData<ScannerInfo> = MutableLiveData()
    val scanProgress: MutableLiveData<Pair<Int, ScanResponse>> = MutableLiveData()

    val isScanning: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<Throwable> = MutableLiveData()

    init {
        isScanning.value = false
    }

    fun queryScannerInfo() {
        error.value = null
        networkScanner.getScannerInfoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    scanInfo.value = it
                }, {
                    error.value = it
                })
    }

    fun scan() {
        stopScan()
        _scannerResults.clear()
        scanResults.value = _scannerResults
        error.value = null
        isScanning.value = true
        disposable = networkScanner.scanConnectedWifiNetwork()
                .map {
                    scanProgress.postValue(it)
                    it.second
                }
                .filter { it.found }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _scannerResults.add(it)
                    scanResults.value = _scannerResults
                }, {
                    error.value = it
                    isScanning.value = false
                }, {
                    isScanning.value = false
                })
    }

    fun stopScan() {
        disposable?.dispose()
    }
}