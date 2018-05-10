package com.aar.app.wifinetanalyzer.wifiselector

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aar.app.wifinetanalyzer.App
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import com.aar.app.wifinetanalyzer.wifilist.WifiListAdapter
import com.aar.app.wifinetanalyzer.wifilist.WifiListViewModel
import kotlinx.android.synthetic.main.activity_wifi_selector.*
import kotlinx.android.synthetic.main.partial_empty_wifi_list_item.*
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
    private val wifiListAdapter = WifiListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_selector)

        (application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(WifiListViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_select_wifi)

        toolbar.setNavigationOnClickListener { finish() }

        wifiListAdapter.onItemClickListener = object: WifiListAdapter.OnItemClickListener {
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
        viewModel.wifiList.observe(this, Observer { onWifiListUpdated(it) })
    }

    override fun onResume() {
        super.onResume()
        viewModel.startListUpdate()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopListUpdate()
    }

    private fun onWifiListUpdated(wList: List<WifiAccessPoint>?) {
        wList?.let {
            if (it.isEmpty()) {
                emptyListLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyListLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                wifiListAdapter.wifiAp = it
            }
        }
    }
}
