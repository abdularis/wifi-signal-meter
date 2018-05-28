package com.aar.app.wifinetanalyzer.scanner

data class ScannerInfo(
        val interfaceName: String,
        val mac: String,
        val ip: String,
        val netmask: String,
        val networkPrefixLength: Short,
        val gatewayIp: String,
        val ip4Iterator: Ip4Iterator
)