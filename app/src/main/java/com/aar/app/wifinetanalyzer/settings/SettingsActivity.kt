package com.aar.app.wifinetanalyzer.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aar.app.wifinetanalyzer.R
import kotlinx.android.synthetic.main.partial_toolbar.*

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_settings)
        toolbar.setNavigationOnClickListener { finish() }
    }
}