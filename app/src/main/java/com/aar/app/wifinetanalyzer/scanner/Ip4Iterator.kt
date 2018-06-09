package com.aar.app.wifinetanalyzer.scanner

/**
 * It seems to be more natural for us to work with
 * Big Endian, we can still convert to Little Endian
 * in the end.
 */
class Ip4Iterator(val ip: Int, val netmask: Int) {

    private var _currentHostId = 0
    private var _currentIp = 0

    val networkId = netmask and ip
    val numberOfIp = (0xffffffff xor netmask.toLong()).toInt() - 1 // ignore last address (broadcast)
    val currentIp get() = _currentIp
    val currentHostId get() = _currentHostId

    fun next() {
        if (_currentHostId >= numberOfIp) return
        _currentHostId++
        _currentIp = networkId or _currentHostId
    }

    fun hasNext() = _currentHostId < numberOfIp
}