package com.aar.app.wifinetanalyzer.common

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData

fun <T> LiveData<T>.nonNullObserve(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    this.observe(lifecycleOwner, android.arch.lifecycle.Observer { it?.let { observer(it) } })
}
