package com.aar.app.wifinetanalyzer.common

class Optional<T>(val value: T?) {

    val isNull get() = value == null

    companion object {
        @JvmStatic
        fun <T> of(value: T): Optional<T> = Optional(value)

        @JvmStatic
        fun <T> empty(): Optional<T> = Optional(null)
    }

}