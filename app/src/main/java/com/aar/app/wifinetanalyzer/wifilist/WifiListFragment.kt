package com.aar.app.wifinetanalyzer.wifilist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.aar.app.wifinetanalyzer.App

import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import com.aar.app.wifinetanalyzer.wififilterdialog.WifiFilterDialogFragment
import com.aar.app.wifinetanalyzer.wifilist.WifiListAdapter.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_wifi_list.*
import kotlinx.android.synthetic.main.partial_empty_wifi_list_item.*
import kotlinx.android.synthetic.main.partial_wifi_filter_info.*
import javax.inject.Inject

class WifiListFragment : Fragment() {

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    lateinit var viewModel: WifiListViewModel
    val wifiListAdapter: WifiListAdapter = WifiListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(WifiListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_wifi_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wifiListAdapter.onItemClickListener = object: OnItemClickListener {
            override fun onItemClick(wifiAp: WifiAccessPoint) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = wifiListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewModel.wifiAccessPointList.observe(this, Observer {
            onWifiListUpdated(it)
        })
        viewModel.onFilterChanged.observe(this, Observer {
            showOrHideFilterLayout()
        })
        buttonClearFilter.setOnClickListener { viewModel.clearFilter() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startListUpdate()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopListUpdate()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_wifi_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_wifi_list_filter) {
            showFilterListDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onWifiListUpdated(wifiApList: WifiAccessPointList?) {
        wifiApList?.let {
            if (it.wifiList.isEmpty()) {
                emptyListLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyListLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                if (viewModel.isFilterOn()) {
                    wifiListAdapter.replaceData(it.wifiList)
                } else {
                    wifiListAdapter.replaceData(it)
                }
            }
        }
    }

    private fun showFilterListDialog() {
        val dialog = WifiFilterDialogFragment()
        dialog.onFilterListener = { viewModel.filterList(it) }
        dialog.show(fragmentManager, "wifi_filter")
    }

    private fun showOrHideFilterLayout() {
        if (viewModel.isFilterOn()) {
            filterLayout.visibility = View.VISIBLE
        } else {
            filterLayout.visibility = View.GONE
        }
    }
}
