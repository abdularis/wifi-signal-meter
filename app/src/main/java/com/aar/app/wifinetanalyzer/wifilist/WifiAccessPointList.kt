package com.aar.app.wifinetanalyzer.wifilist

import com.aar.app.wifinetanalyzer.model.WifiAccessPoint

data class WifiAccessPointList(val wifiList: List<WifiAccessPoint>) {

    val connectedWifi: WifiAccessPoint?
    val interferingWifiList: List<WifiAccessPoint>
    val otherWifiList: List<WifiAccessPoint>

    init {
        connectedWifi = filterConnectedWifi(wifiList)

        val interfering = ArrayList<WifiAccessPoint>()
        val nonInterfering = ArrayList<WifiAccessPoint>()
        if (connectedWifi != null) {
            for (wifiAp in wifiList) {
                if (isWifiInterfering(wifiAp)) {
                    interfering.add(wifiAp)
                } else if (connectedWifi != wifiAp) {
                    nonInterfering.add(wifiAp)
                }
            }

            interferingWifiList = interfering
            otherWifiList = nonInterfering
        } else {
            interferingWifiList = interfering
            otherWifiList = wifiList
        }
    }

    private fun filterConnectedWifi(wifiApList: List<WifiAccessPoint>): WifiAccessPoint? {
        for (wifiAp in wifiApList) {
            if (wifiAp.isConnected) return wifiAp
        }
        return null
    }

    private fun isWifiInterfering(wifiAp: WifiAccessPoint) =
            connectedWifi != null &&
                    connectedWifi.signal.channel.channelNumber == wifiAp.signal.channel.channelNumber &&
                    connectedWifi.signal.bssid != wifiAp.signal.bssid
}