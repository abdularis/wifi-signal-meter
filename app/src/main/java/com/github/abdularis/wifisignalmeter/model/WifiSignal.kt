package com.github.abdularis.wifisignalmeter.model

data class WifiSignal(val bssid: String,
                      val ssid: String,
                      val capabilities: String,
                      val channel: WifiChannel,
                      val level: Int,
                      val vendor: String) {
    val isHidden: Boolean = ssid.isEmpty()
    val authenticationNeeded: Boolean =
            capabilities.findAnyOf(listOf("WEP", "WPA"), ignoreCase = true) != null
}