package com.aar.app.wifinetanalyzer.scanner

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aar.app.wifinetanalyzer.settings.SettingsProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NetworkScannerViewModel(private val networkScanner: NetworkScanner, private val settingsProvider: SettingsProvider): ViewModel() {
    private val _scannerResults = ArrayList<ScanResponse>()
    private var disposable: Disposable? = null

    val threadCount get() = settingsProvider.threadCount

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
        if (isScanning.value == true)
            return

        _scannerResults.clear()
        scanResults.value = _scannerResults
        error.value = null
        isScanning.value = true
        disposable = networkScanner.scanConnectedWifiNetwork(settingsProvider.threadCount)
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
        disposable = null
        isScanning.value = false
    }
}