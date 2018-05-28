package com.aar.app.wifinetanalyzer.scanner

data class ScanResponse(
        val ip: String = "",
        val mac: String = "",
        val vendor: String = "",
        val type: DeviceType = DeviceType.None,
        val found: Boolean = false
) {
    enum class DeviceType {
        None,
        Me,
        Router,
        Other
    }
}
