package com.github.abdularis.wifisignalmeter.signalmeter

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class WifiSignalProvider(private val context: Context) {

    private val wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

//    fun wifiSignalUpdate(bssidFilter: String) : Flowable<Optional<WifiAccessPoint>> {
//        return Flowable.interval(1000, TimeUnit.MILLISECONDS)
//                .flatMap {
//                    wifiManager.startScan()
//                    val scanResults = wifiManager.scanResults
//                    if (scanResults.isEmpty()) {
//                        Flowable.just(Optional.empty())
//                    } else {
//                        Flowable.fromIterable(scanResults)
//                                .filter {
//                                    bssidFilter == it.BSSID
//                                }
//                                .map {
//                                    val wifiAp = WifiAccessPoint.fromScanResult(it, if (isThisConnectedWifi(it)) wifiManager.connectionInfo else null)
//                                    Optional.of(wifiAp)
//                                }
//                                .defaultIfEmpty(Optional.empty())
//                    }
//                }
//    }

    fun wifiSignalUpdate(interval: Long): Flowable<List<WifiAccessPoint>> {
            return Flowable.interval(0, interval, TimeUnit.MILLISECONDS)
                    .flatMap {
                        Log.v("Hahaha", "count: $it")
                        wifiManager.startScan()
                        Flowable.fromIterable(wifiManager.scanResults)
                                .map {
                                    WifiAccessPoint.fromScanResult(
                                            it,
                                            if (isThisConnectedWifi(it)) wifiManager.connectionInfo else null
                                    )
                                }
                                .toList()
                                .toFlowable()
                    }
        }

    private fun isThisConnectedWifi(scanResult: ScanResult) =
            wifiManager.connectionInfo.bssid == scanResult.BSSID
}