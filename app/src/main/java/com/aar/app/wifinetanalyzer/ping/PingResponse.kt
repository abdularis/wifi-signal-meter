package com.aar.app.wifinetanalyzer.ping

data class PingResponse(
        val ip: String? = null,
        val mac: String? = null,
        val vendor: String? = null,
        val isReachable: Boolean = false,
        val timeTaken: Float = 0f
)