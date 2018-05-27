package com.aar.app.wifinetanalyzer.scanner

data class ScanResponse(
        val ip: String = "",
        val mac: String = "",
        val vendor: String = "",
        val found: Boolean = false
)
