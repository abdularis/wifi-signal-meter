package com.aar.app.wifinetanalyzer.wifilist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aar.app.wifinetanalyzer.App

import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import com.aar.app.wifinetanalyzer.wifilist.WifiListAdapter.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_wifi_list.*
import kotlinx.android.synthetic.main.partial_empty_wifi_list_item.*
import javax.inject.Inject

class WifiListFragment : Fragment() {

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    lateinit var viewModel: WifiListViewModel
    var onWifiItemClickListener: OnWifiItemClickListener? = null
    val wifiListAdapter: WifiListAdapter = WifiListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(WifiListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wifi_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wifiListAdapter.onItemClickListener = object: OnItemClickListener {
            override fun onItemClick(wifiAp: WifiAccessPoint) {
                onWifiItemClickListener?.onWifiItemClick(wifiAp)
            }
        }
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = wifiListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewModel.wifiAccessPointList.observe(this, Observer {
            onWifiListUpdated(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.startListUpdate()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopListUpdate()
    }

    private fun onWifiListUpdated(wifiApList: WifiAccessPointList?) {
        wifiApList?.let {
            if (it.wifiList.isEmpty()) {
                emptyListLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyListLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                wifiListAdapter.replaceData(it)
            }
        }
    }

    interface OnWifiItemClickListener {
        fun onWifiItemClick(wifiAp: WifiAccessPoint)
    }
}
