package com.github.abdularis.wifisignalmeter.common

import android.net.wifi.WifiManager

fun intToStringIP(ip: Int?): String =
    if (ip == null) "0.0.0.0"
    else "${ip and 0xFF}.${ip shr 8 and 0xFF}.${ip shr 16 and 0xFF}.${ip shr 24 and 0xFF}"

fun percentToSignalLevel(levelPercent: Int): String =
    when {
        levelPercent <= 40 -> "Poor"
        levelPercent <= 70 -> "Fair"
        levelPercent <= 85 -> "Good"
        else -> "Excellent"
    }

fun calcSignalPercentage(rssi: Int) = WifiManager.calculateSignalLevel(rssi, 101)