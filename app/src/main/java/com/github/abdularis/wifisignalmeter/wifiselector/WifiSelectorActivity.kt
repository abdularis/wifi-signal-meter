package com.github.abdularis.wifisignalmeter.wifiselector

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.github.abdularis.wifisignalmeter.App
import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.ViewModelFactory
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.wifilist.WifiListViewModel
import kotlinx.android.synthetic.main.activity_wifi_selector.*
import kotlinx.android.synthetic.main.partial_toolbar.*
import javax.inject.Inject

class WifiSelectorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SSID = "SSID"
        const val EXTRA_BSSID = "BSSID"
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    lateinit var viewModel: WifiListViewModel
    private val wifiListAdapter: SimpleWifiListAdapter = SimpleWifiListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_selector)

        (application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(WifiListViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select Wifi"

        wifiListAdapter.onItemClickListener = object: SimpleWifiListAdapter.OnItemClickListener {
            override fun onItemClick(wifiAp: WifiAccessPoint) {
                val intent = Intent()
                intent.putExtra(EXTRA_SSID, wifiAp.signal.ssid)
                intent.putExtra(EXTRA_BSSID, wifiAp.signal.bssid)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = wifiListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.wifiList.observe(this, Observer<List<WifiAccessPoint>> {v -> v?.let{ wifiListAdapter.wifiAp = it }})
    }

    override fun onResume() {
        super.onResume()
        viewModel.startListUpdate()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopListUpdate()
    }
}
