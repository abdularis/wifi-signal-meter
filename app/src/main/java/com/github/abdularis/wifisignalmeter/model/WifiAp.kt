package com.github.abdularis.wifisignalmeter.model

import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo

class WifiAp(val bssid : String,
             val ssid : String,
             val capabilities : String,
             val channelWidth : Int,
             val frequency : Int,
             val level : Int,
             val wifiConnectionInfo : WifiInfo?) {

    val isConnected get() = wifiConnectionInfo != null

    companion object {
        @JvmStatic
        fun empty(): WifiAp {
            return WifiAp(
                    bssid = "02:00:00:00:00:00",
                    ssid = "<no network>",
                    capabilities = "",
                    channelWidth = -1,
                    frequency = -1,
                    level = -1,
                    wifiConnectionInfo = null
            )
        }

        @JvmStatic
        fun fromScanResult(scanResult: ScanResult, wifiInfo : WifiInfo? = null) : WifiAp {
            return WifiAp(
                    bssid = scanResult.BSSID,
                    ssid = scanResult.SSID,
                    capabilities = scanResult.capabilities,
                    channelWidth = -1,
                    frequency = scanResult.frequency,
                    level = scanResult.level,
                    wifiConnectionInfo = wifiInfo
            )
        }
    }

}