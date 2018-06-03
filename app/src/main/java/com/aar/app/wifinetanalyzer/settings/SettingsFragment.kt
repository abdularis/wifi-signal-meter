package com.aar.app.wifinetanalyzer.settings


import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.aar.app.wifinetanalyzer.R

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}