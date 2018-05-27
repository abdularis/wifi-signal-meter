package com.aar.app.wifinetanalyzer.scanner


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aar.app.wifinetanalyzer.App
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.common.intToStringIP
import kotlinx.android.synthetic.main.fragment_net_scanner.*
import javax.inject.Inject


class NetworkScannerFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: NetworkScannerViewModel
    private val deviceListAdapter = DeviceListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_net_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = deviceListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        buttonScan.setOnClickListener { viewModel.scan() }

        initViewModel()
    }

    private fun initViewModel() {
        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NetworkScannerViewModel::class.java)
        viewModel.scannerInfo.observe(this, Observer {
            it?.let{ onScannerInfoChanged(it) }
        })
        viewModel.scannerResults.observe(this, Observer {
            it?.let { onScannerResultsChanged(it) }
        })
        viewModel.scanProgress.observe(this, Observer {
            it?.let { onScanProgressed(it) }
        })
        viewModel.state.observe(this, Observer {
            it?.let { onStateChanged(it) }
        })
        viewModel.queryScannerInfo()
    }

    private fun onScannerInfoChanged(scannerInfo: ScannerInfo) {
        textIfaceName.text = scannerInfo.interfaceName
        textIpNetmask.text = "${scannerInfo.ip}/${scannerInfo.networkPrefixLength}"
        textNetId.text = intToStringIP(Integer.reverseBytes(scannerInfo.ip4Iterator.networkId))
        textDeviceCount.text = "${scannerInfo.ip4Iterator.numberOfIp} devices"
    }

    private fun onScannerResultsChanged(scannerResults: List<ScanResponse>) {
        deviceListAdapter.replaceAll(scannerResults)
    }

    private fun onScanProgressed(progress: Pair<Int, ScanResponse>) {
        textProgressIp.text = "Scanning... ${progress.second.ip} - ${progress.first}%"
        progressBarScan.progress = progress.first
    }

    private fun onStateChanged(state: NetworkScannerViewModel.State) {

    }
}
