package com.aar.app.wifinetanalyzer.common


import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.aar.app.wifinetanalyzer.R
import java.net.Inet4Address

fun intToStringIP(ip: Int?): String =
    if (ip == null) "0.0.0.0"
    else "${ip shr 24 and 0xFF}.${ip shr 16 and 0xFF}.${ip shr 8 and 0xFF}.${ip and 0xFF}"

fun byteArrayToStringIp(ip: ByteArray): String =
        Inet4Address.getByAddress(ip).hostAddress


fun percentToSignalLevel(res: Resources?, levelPercent: Int): String =
    when {
        levelPercent <= 40 -> res?.getString(R.string.signal_poor) ?: "Poor"
        levelPercent <= 70 -> res?.getString(R.string.signal_fair) ?: "Fair"
        levelPercent <= 85 -> res?.getString(R.string.signal_good) ?: "Good"
        else -> res?.getString(R.string.signal_excellent) ?: "Excellent"
    }

fun calcSignalPercentage(rssi: Int) = WifiManager.calculateSignalLevel(rssi, 101)

class WifiUtils {

    companion object {

        fun checkWifiConnectivity(ctx: Context): Boolean {
            val connManager: ConnectivityManager = ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiCheck = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return wifiCheck.isConnected
        }

        fun getWifiConnectedIpAddress(ctx: Context): ByteArray {
            val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            return wifiInfo.ipAddress.toBigInteger().toByteArray().reversedArray()
        }



    }

}
