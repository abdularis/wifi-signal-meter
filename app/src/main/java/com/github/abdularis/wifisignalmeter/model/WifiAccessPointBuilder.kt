package com.github.abdularis.wifisignalmeter.model

import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import com.github.abdularis.wifisignalmeter.common.intToStringIP

class WifiAccessPointBuilder {

    var bssid: String = ""
        private set
    var ssid: String = ""
        private set
    var capabilities: String = ""
        private set
    var frequency: Int = 0
        private set
    var level: Int = 0
        private set
    var vendor: String = ""
        private set
    var wifiConnectionInfo: WifiInfo? = null
        private set
    var dhcpInfo: DhcpInfo? = null

    fun bssid(bssid: String) = apply { this.bssid = bssid }
    fun ssid(ssid: String) = apply { this.ssid = ssid }
    fun capabilities(capabilities: String) = apply { this.capabilities = capabilities }
    fun frequency(frequency: Int) = apply { this.frequency = frequency }
    fun level(level: Int) = apply { this.level = level }
    fun vendor(vendor: String) = apply { this.vendor = vendor }
    fun connectionInfo(connctionInfo: WifiInfo?) = apply { this.wifiConnectionInfo = connctionInfo }
    fun dhcpInfo(dhcpInfo: DhcpInfo?) = apply { this.dhcpInfo = dhcpInfo }
    fun build(): WifiAccessPoint {
        val wifiSignal = WifiSignal(
                bssid = bssid,
                ssid = ssid,
                capabilities = capabilities,
                channel = WifiChannel(frequency),
                level = level,
                vendor = vendor
        )

        val connInfo = if (wifiConnectionInfo != null && dhcpInfo != null) {
            ConnectionInfo(
                    intToStringIP(dhcpInfo?.ipAddress),
                    wifiConnectionInfo?.linkSpeed ?: -1,
                    intToStringIP(dhcpInfo?.dns1),
                    intToStringIP(dhcpInfo?.dns2),
                    intToStringIP(dhcpInfo?.gateway),
                    intToStringIP(dhcpInfo?.netmask),
                    intToStringIP(dhcpInfo?.serverAddress)
            )
        } else {
            null
        }

        return WifiAccessPoint(wifiSignal, connInfo)
    }
}