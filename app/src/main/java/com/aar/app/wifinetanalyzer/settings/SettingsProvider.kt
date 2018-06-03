package com.aar.app.wifinetanalyzer.settings

import android.content.Context
import android.preference.PreferenceManager

class SettingsProvider(ctx: Context) {

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx)

    var scanInterval: Int
        get() = sharedPrefs.getString("update_interval", "1").toInt()
        set(value) {
            sharedPrefs.edit()
                    .putString("update_interval", value.toString())
                    .apply()
        }

    var threadCount: Int
        get() = sharedPrefs.getString("scanner_threads", "1").toInt()
        set(value) {
            sharedPrefs.edit()
                    .putString("scanner_threads", value.toString())
                    .apply()
        }
}