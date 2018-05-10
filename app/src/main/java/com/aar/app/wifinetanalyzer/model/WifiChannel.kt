package com.aar.app.wifinetanalyzer.model

data class WifiChannel(val frequency: Int) {

    val band: Band = if (is24GHz()) Band.GHZ_24 else Band.GHZ_5

    val channelNumber: Int
        get() {
            if (is24GHz()) {
                return Math.min((frequency - 2412) / 5 + 1, 14)
            }
            return -1 // not yet supported
        }

    fun is24GHz() = frequency in 2401..2499

    fun is5GHz() = frequency in 4901..5899

    enum class Band {
        GHZ_24, GHZ_5
    }

}

