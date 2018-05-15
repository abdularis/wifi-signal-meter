package com.aar.app.wifinetanalyzer.wifilist

import com.aar.app.wifinetanalyzer.model.WifiAccessPoint

data class WifiAccessPointList(val wifiList: List<WifiAccessPoint>) {

    val connectedWifi: WifiAccessPoint? = wifiList.firstOrNull { it.isConnected }
    val interferingWifiList: List<WifiAccessPoint> by lazy {
        if (connectedWifi == null)
            emptyList()
        else
            wifiList.filter { isInterfering(it) }
    }
    val otherWifiList: List<WifiAccessPoint> by lazy {
        if (connectedWifi == null)
            wifiList
        else
            wifiList.filter { !isInterfering(it) && connectedWifi != it }
    }

    private fun isInterfering(wifiAp: WifiAccessPoint) =
            connectedWifi != null &&
                    connectedWifi.signal.channel.channelNumber == wifiAp.signal.channel.channelNumber &&
                    connectedWifi.signal.bssid != wifiAp.signal.bssid
}