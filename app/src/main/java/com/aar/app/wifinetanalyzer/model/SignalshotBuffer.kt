package com.aar.app.wifinetanalyzer.model

import java.util.Deque
import java.util.ArrayDeque

class SignalshotBuffer(var maxData: Int = 32) {

    private val _data: Deque<Float> = ArrayDeque()
    val data: Collection<Float> get() = _data

    private var _wifiAp: WifiAccessPoint? = null
    val wifiAp: WifiAccessPoint? get() = _wifiAp

    fun add(wifiAp: WifiAccessPoint?) {
        if (wifiAp != null) {
            _wifiAp = wifiAp

            if (_data.size >= maxData) {
                _data.removeLast()
            }

            _data.addFirst(wifiAp.signal.level.toFloat())
        } else {
            _data.addFirst(-100f)
        }
    }

    fun clear() = _data.clear()
}