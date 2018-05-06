package com.github.abdularis.wifisignalmeter.signalmeter

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import com.github.abdularis.wifisignalmeter.common.Optional
import com.github.abdularis.wifisignalmeter.data.VendorFinder
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.model.WifiAccessPointBuilder
import io.reactivex.Flowable
import io.reactivex.functions.Predicate
import java.util.concurrent.TimeUnit

class WifiSignalProvider(context: Context, private val vendorFinder: VendorFinder) {

    private val wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun connectedWifiSignalUpdate(interval: Long): Flowable<Optional<WifiAccessPoint>> {
        return wifiSignalUpdateOptionalFilter(interval, Predicate { isThisConnectedWifi(it) })
    }

    fun wifiSignalUpdate(interval: Long, ssid: String, bssid: String) : Flowable<Optional<WifiAccessPoint>> {
        return wifiSignalUpdateOptionalFilter(interval, Predicate { ssid == it.SSID && bssid == it.BSSID })
    }

    fun wifiSignalUpdate(interval: Long): Flowable<List<WifiAccessPoint>> {
            return Flowable.interval(0, interval, TimeUnit.MILLISECONDS)
                    .flatMap {
                        wifiManager.startScan()
                        Flowable.fromIterable(wifiManager.scanResults)
                                .map(this::createWifiAp)
                                .toList()
                                .toFlowable()
                    }
        }

    private fun wifiSignalUpdateOptionalFilter(interval: Long, predicate: Predicate<ScanResult>): Flowable<Optional<WifiAccessPoint>> {
        return Flowable.interval(0, interval, TimeUnit.MILLISECONDS)
                .flatMap {
                    wifiManager.startScan()
                    val scanResults = wifiManager.scanResults
                    if (scanResults.isEmpty()) {
                        Flowable.just(Optional.empty())
                    } else {
                        Flowable.fromIterable(scanResults)
                                .filter(predicate)
                                .map {
                                    Optional.of(createWifiAp(it))
                                }
                                .take(1)
                                .defaultIfEmpty(Optional.empty())
                    }
                }
    }

    private fun isThisConnectedWifi(scanResult: ScanResult) =
        wifiManager.connectionInfo.ssid.trim('"') == scanResult.SSID &&
                wifiManager.connectionInfo.bssid == scanResult.BSSID

    private fun createWifiAp(scanResult: ScanResult): WifiAccessPoint {
        return WifiAccessPointBuilder()
                .bssid(scanResult.BSSID)
                .ssid(scanResult.SSID)
                .capabilities(scanResult.capabilities)
                .frequency(scanResult.frequency)
                .level(scanResult.level)
                .vendor(vendorFinder.findVendor(scanResult.BSSID) ?: "")
                .connectionInfo(if (isThisConnectedWifi(scanResult)) wifiManager.connectionInfo else null)
                .dhcpInfo(if (isThisConnectedWifi(scanResult)) wifiManager.dhcpInfo else null)
                .build()
    }
}