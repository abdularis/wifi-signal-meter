package com.github.abdularis.wifisignalmeter.model

data class WifiSignal(val bssid: String,
                      val ssid: String,
                      val capabilities: String,
                      val channel: WifiChannel,
                      val level: Int,
                      val vendor: String)