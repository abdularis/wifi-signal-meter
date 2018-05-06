package com.github.abdularis.wifisignalmeter.wifilist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.abdularis.wifisignalmeter.App

import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.ViewModelFactory
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.wifilist.WifiListAdapter.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_wifi_list.*
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

    interface OnWifiItemClickListener {
        fun onWifiItemClick(wifiAp: WifiAccessPoint)
    }
}
