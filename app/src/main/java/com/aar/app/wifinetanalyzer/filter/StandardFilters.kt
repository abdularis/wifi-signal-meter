package com.aar.app.wifinetanalyzer.filter

import com.aar.app.wifinetanalyzer.model.WifiAccessPoint

class NoFilter: AbstractWifiFilter() {
    override fun test(t: WifiAccessPoint): Boolean {
        return true
    }
}

class ChannelFilter(val channel: Int): AbstractWifiFilter() {
    override fun test(t: WifiAccessPoint): Boolean {
        return channel == t.signal.channel.channelNumber
    }
}

class SsidFilter(val ssid: String): AbstractWifiFilter() {
    override fun test(t: WifiAccessPoint): Boolean {
        return ssid == t.signal.ssid
    }
}

//class BssidFilter(val bssid: String): AbstractWifiFilter() {
//    override fun test(t: WifiAccessPoint): Boolean {
//        return bssid == t.signal.bssid
//    }
//}
//
//class SsidBssidFilter(val ssid: String, val bssid: String): AbstractWifiFilter() {
//    override fun test(t: WifiAccessPoint): Boolean {
//        return ssid == t.signal.ssid && bssid == t.signal.bssid
//    }
//}
//
//class AlwaysConnectedFilter(val otherFilter: AbstractWifiFilter = NoFilter()): AbstractWifiFilter() {
//    override fun test(t: WifiAccessPoint): Boolean {
//        return otherFilter.test(t) || t.isConnected
//    }
//}

class CompositeFilter(): AbstractWifiFilter() {

    private val _filters: ArrayList<AbstractWifiFilter> = ArrayList()
    val filters: List<AbstractWifiFilter>
        get() = _filters

    constructor(vararg filters: AbstractWifiFilter): this() {
        this._filters.addAll(filters)
    }

    fun add(filter: AbstractWifiFilter) {
        _filters.add(filter)
    }

    fun clear() = _filters.clear()

    fun isEmpty() = _filters.isEmpty()

    override fun test(t: WifiAccessPoint): Boolean {
        var result = true
        for (filter in _filters) result = result && filter.test(t)
        return result
    }
}