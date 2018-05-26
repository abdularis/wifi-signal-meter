package com.aar.app.wifinetanalyzer.ping

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Patterns
import com.aar.app.wifinetanalyzer.data.VendorFinder
import com.stealthcopter.networktools.ARPInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PingViewModel(private val vendorFinder: VendorFinder): ViewModel() {

    enum class State {
        PING_STARTED,
        PING_COMPLETED,
        PING_STOPPED,
        PING_ERROR,
        IP_ERROR
    }

    private var disposable: Disposable? = null
    private val _pingResultsList: ArrayList<PingResponse> = ArrayList()
    private val _pingResults: MutableLiveData<List<PingResponse>> = MutableLiveData()
    private var _currentIp: String = ""
    private var _currentPingCount: Int = 5

    val currentIp: String get() = _currentIp
    val currentPingCount: Int get() = _currentPingCount
    val pingResults: LiveData<List<PingResponse>> get() = _pingResults
    val state: MutableLiveData<State> = MutableLiveData()
    val pingInProgress: Boolean
        get() = disposable != null && !disposable!!.isDisposed

    fun stopPing() {
        if (pingInProgress) {
            disposable?.dispose()
            state.value = State.PING_STOPPED
        }
    }

    fun ping(ip: String, count: Int) {
        if (!Patterns.IP_ADDRESS.matcher(ip).matches()) {
            state.value = State.IP_ERROR
            return
        }

        disposable?.dispose()
        state.value = State.PING_STARTED
        _currentIp = ip
        _currentPingCount = count
        _pingResultsList.clear()
        disposable = RxPing.ping(_currentIp, _currentPingCount)
                .map {
                    if (it.hasError())
                        PingResponse()
                    else {
                        val mac = ARPInfo.getMACFromIPAddress(it.ia.hostAddress) ?: ""
                        val vendor = vendorFinder.findVendor(mac) ?: ""
                        PingResponse(
                                ip = it.ia.hostAddress,
                                mac = mac,
                                vendor = vendor,
                                isReachable = it.isReachable,
                                timeTaken = it.timeTaken
                        )
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _pingResultsList.add(it)
                    _pingResults.value = _pingResultsList
                }, {
                    state.value = State.PING_ERROR
                }, {
                    state.value = State.PING_COMPLETED
                })
    }

}