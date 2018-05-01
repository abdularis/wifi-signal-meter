package com.github.abdularis.wifisignalmeter.signalmeter

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.wifi.WifiManager
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignalMeterViewModel @Inject constructor(private val wifiSignalProvider: WifiSignalProvider) : ViewModel() {

    private var disposable: Disposable? = null
    val wifiList: MutableLiveData<List<WifiAccessPoint>> = MutableLiveData()
    val wifiUpdates: MutableLiveData<WifiAccessPointUiModel> = MutableLiveData()
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
                .map {
                    if (it.isNull)
                        createEmptyWifiAccessPointUiModel() else
                        createWifiAccessPointUiModel(it.value!!)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    wifiUpdates.value = it
                }, {
                    wifiUpdates.value = null
                })
    }

    fun stopSignalUpdates() {
        disposable?.dispose()
    }

    private fun createWifiAccessPointUiModel(wifiAp: WifiAccessPoint): WifiAccessPointUiModel {
        val connInfo = wifiAp.wifiConnectionInfo
        val levelPercent = WifiManager.calculateSignalLevel(wifiAp.level, 101)
        return WifiAccessPointUiModel(
                ssid = wifiAp.ssid,
                bssid = wifiAp.bssid,
                levelPercent = levelPercent,
                levelPercentText = "$levelPercent%",
                levelText = when {
                    wifiAp.level <= 40 -> "Poor"
                    wifiAp.level <= 70 -> "Fair"
                    wifiAp.level <= 85 -> "Good"
                    else -> "Excellent"
                },
                levelRssiText = "${wifiAp.level} dbm",
                isConnected = wifiAp.isConnected,
                connectionStateText = if (wifiAp.isConnected) "Connected" else "Not connected",
                linkSpeed = "${connInfo?.linkSpeed} Mbps",
                deviceIp = "",
                routerMac = if (connInfo?.bssid != null) connInfo.bssid else "02:00:00:00:00:00",
                frequency = "${wifiAp.frequency} MHz",
                channel = -1
        )
    }

    private fun createEmptyWifiAccessPointUiModel(): WifiAccessPointUiModel {
        return WifiAccessPointUiModel(
                ssid = "<select wifi>",
                bssid = "02:00:00:00:00:00",
                levelPercent = 0,
                levelPercentText = "0%",
                levelText = "No Signal",
                levelRssiText = "-0 dbm",
                isConnected = false,
                connectionStateText = "",
                linkSpeed = "",
                deviceIp = "",
                routerMac = "",
                frequency = "",
                channel = 0
        )
    }
}