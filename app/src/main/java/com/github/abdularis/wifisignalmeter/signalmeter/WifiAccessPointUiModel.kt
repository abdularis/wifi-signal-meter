package com.github.abdularis.wifisignalmeter.signalmeter

data class WifiAccessPointUiModel(
        val ssid: String,
        val bssid: String,
        val levelPercent: Int,
        val levelPercentText: String,
        val levelText: String,
        val levelRssiText: String,

        val isConnected: Boolean,
        val connectionStateText: String,
        val linkSpeed: String,
        val deviceIp: String,
        val routerMac: String,
        val frequency: String,
        val channel: Int
)
