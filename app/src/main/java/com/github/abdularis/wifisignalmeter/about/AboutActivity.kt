package com.github.abdularis.wifisignalmeter.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.abdularis.wifisignalmeter.R
import kotlinx.android.synthetic.main.partial_toolbar.*

class AboutActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_about)
        toolbar.setNavigationOnClickListener { finish() }
    }
}