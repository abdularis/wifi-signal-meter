package com.aar.app.wifinetanalyzer.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aar.app.wifinetanalyzer.R
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.partial_toolbar.*

class AboutActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_about)
        toolbar.setNavigationOnClickListener { finish() }

        val versionName = packageManager.getPackageInfo(packageName, 0)?.versionName ?: ""
        textAppName.text = "%s - v%s".format(getString(R.string.app_name), versionName)
    }
}