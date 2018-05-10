package com.aar.app.wifinetanalyzer.model

data class WifiAccessPoint(val signal: WifiSignal,
                           val connectionInfo: ConnectionInfo?) {

    val isConnected get() = connectionInfo != null
}