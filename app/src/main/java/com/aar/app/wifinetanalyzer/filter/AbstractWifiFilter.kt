package com.aar.app.wifinetanalyzer.filter

import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import io.reactivex.functions.Predicate

abstract class AbstractWifiFilter: Predicate<WifiAccessPoint>
