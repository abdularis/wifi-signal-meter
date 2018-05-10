package com.aar.app.wifinetanalyzer.common


import android.content.res.Resources
import android.net.wifi.WifiManager
import com.aar.app.wifinetanalyzer.R

fun intToStringIP(ip: Int?): String =
    if (ip == null) "0.0.0.0"
    else "${ip and 0xFF}.${ip shr 8 and 0xFF}.${ip shr 16 and 0xFF}.${ip shr 24 and 0xFF}"

fun percentToSignalLevel(res: Resources?, levelPercent: Int): String =
    when {
        levelPercent <= 40 -> res?.getString(R.string.signal_poor) ?: "Poor"
        levelPercent <= 70 -> res?.getString(R.string.signal_fair) ?: "Fair"
        levelPercent <= 85 -> res?.getString(R.string.signal_good) ?: "Good"
        else -> res?.getString(R.string.signal_excellent) ?: "Excellent"
    }

fun calcSignalPercentage(rssi: Int) = WifiManager.calculateSignalLevel(rssi, 101)