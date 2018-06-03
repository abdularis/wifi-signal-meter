package com.aar.app.wifinetanalyzer.scanner


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.aar.app.wifinetanalyzer.App
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.common.intToStringIP
import com.aar.app.wifinetanalyzer.common.nonNullObserve
import com.aar.app.wifinetanalyzer.scanner.error.WifiInterfaceNotFoundException
import com.aar.app.wifinetanalyzer.scanner.error.WifiNotConnectedException
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

        buttonScan.setOnClickListener { scanOrStop() }
        buttonRefresh.setOnClickListener { viewModel.queryScannerInfo() }

        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        textThread.text = "Using %s threads".format(viewModel.threadCount.toString())
    }

    private fun initViewModel() {
        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NetworkScannerViewModel::class.java)
        viewModel.scanInfo.nonNullObserve(this, this::onScannerInfoChanged)
        viewModel.scanResults.nonNullObserve(this, this::onScannerResultsChanged)
        viewModel.scanProgress.nonNullObserve(this, this::onScanProgressed)
        viewModel.isScanning.nonNullObserve(this, this::onScanning)
        viewModel.error.observe(this, Observer { onError(it) })
        viewModel.queryScannerInfo()
    }

    private fun onScannerInfoChanged(scannerInfo: ScannerInfo) {
        textIfaceName.text = scannerInfo.interfaceName
        textIpNetmask.text = "${scannerInfo.ip}/${scannerInfo.networkPrefixLength}"
        textNetId.text = intToStringIP(Integer.reverseBytes(scannerInfo.ip4Iterator.networkId))
        textDeviceCount.text = getString(R.string.possible_devices).format(scannerInfo.ip4Iterator.numberOfIp)
    }

    private fun onScannerResultsChanged(scannerResults: List<ScanResponse>) {
        deviceListAdapter.replaceAll(scannerResults)
    }

    private fun onScanProgressed(progress: Pair<Int, ScanResponse>) {
        textProgressIp.text = "Scanning... ${progress.second.ip} - ${progress.first}%"
        progressBarScan.progress = progress.first
    }

    private fun onError(error: Throwable?) {
        if (error != null) {
            when (error) {
                is WifiNotConnectedException -> textMessage.setText(R.string.err_wifi_not_connected)
                is WifiInterfaceNotFoundException -> textMessage.setText(R.string.err_wifi_not_avail)
                else -> textMessage.setText(R.string.err_wifi_unknown)
            }
            layoutMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            disableScan()
        } else {
            layoutMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            enableScan()
        }
    }

    private fun onScanning(isScanning: Boolean) {
        if (isScanning) {
            textProgressIp.visibility = View.VISIBLE
            progressBarScan.visibility = View.VISIBLE
            buttonScan.setBackgroundResource(R.drawable.bg_ping_btn_stop)
            buttonScan.setText(R.string.lbl_stop)
        } else {
            textProgressIp.visibility = View.GONE
            textProgressIp.text = "-"
            progressBarScan.visibility = View.GONE
            progressBarScan.progress = 0
            buttonScan.setBackgroundResource(R.drawable.bg_ping_btn)
            buttonScan.setText(R.string.lbl_scan)
        }
    }

    private fun enableScan() {
        buttonScan.isEnabled = true
    }

    private fun disableScan() {
        textIfaceName.setText(R.string.err_na)
        textIpNetmask.text = "-"
        textNetId.text = "-"
        textDeviceCount.text = "-"
        buttonScan.isEnabled = false
    }

    private fun scanOrStop() {
        if (viewModel.isScanning.value == true) {
            viewModel.stopScan()
        } else {
            viewModel.scan()
        }
    }
}
