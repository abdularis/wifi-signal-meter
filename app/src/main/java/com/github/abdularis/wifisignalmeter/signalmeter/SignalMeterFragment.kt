package com.github.abdularis.wifisignalmeter.signalmeter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.abdularis.wifisignalmeter.App

import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.ViewModelFactor
import com.github.abdularis.wifisignalmeter.model.WifiAp
import com.github.abdularis.wifisignalmeter.signalmeter.aplistdialog.WifiApListDialogFragment
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_signal_meter.*
import javax.inject.Inject


class SignalMeterFragment : Fragment() {

    @Inject lateinit var viewModelFactor: ViewModelFactor
    lateinit var viewModel: SignalMeterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signal_meter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactor).get(SignalMeterViewModel::class.java)
        viewModel.wifiUpdates.observe(this, Observer<WifiAp> { v -> onWifiSignalUpdate(v) })

        buttonSsid.setOnClickListener {
            val ft = activity?.supportFragmentManager?.beginTransaction()
            val dialog = WifiApListDialogFragment()
            dialog.onItemTouchListner = object: WifiApListDialogFragment.OnItemTouchListener {
                override fun onTouch(wifiAp: WifiAp) {
                    viewModel.bssidFilter = wifiAp.bssid
                }
            }
            dialog.show(ft, "dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startWifiUpdates()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopSignalUpdates()
    }

    private fun onWifiSignalUpdate(wifiAp: WifiAp?) {
        if (wifiAp != null) {
            val levelPercent = WifiManager.calculateSignalLevel(wifiAp.level, 101)
            signalGauge.currentNumber = levelPercent
            textPercent.text = "$levelPercent%"
            textRssi.text = "${wifiAp.level} dBm"
            textSsid.text = wifiAp.ssid
            textSignalSummary.text =
                    when {
                        levelPercent <= 40 -> "Poor"
                        levelPercent <= 70 -> "Fair"
                        levelPercent <= 85 -> "Good"
                        levelPercent <= 100 -> "Excellent"
                        else -> "-"
                    }
            textConnection.text = if (wifiAp.isConnected) "Connected" else "Not Connected"
            wifiAp.wifiConnectionInfo?.let{ setConnectionInfo(it) }
        } else {
            emptySignalUpdate()
        }
    }

    private fun emptySignalUpdate() {
        signalGauge.currentNumber = 0
        textPercent.text = "0%"
        textRssi.text = "0 dBm"
        textSsid.text = "<no wifi>"
        textSignalSummary.text = "No Signal"
        textConnection.text = "Not Connected"

        //
        textWifiName.text = "<no wifi>"
        textSpeed.text = "-1 Mbps"
        textIp.text = "-"
        textMac.text = "02:00:00:00:00:00"
        textFreq.text = "-1 MHz"
    }

    fun setConnectionInfo(wifiInfo: WifiInfo) {
        textWifiName.text = wifiInfo.ssid
        textSpeed.text = "${wifiInfo.linkSpeed} Mbps"
        textIp.text = "-"
        textMac.text = wifiInfo.macAddress
//        textFreq.text = "${wifiInfo.frequency} MHz"
    }

    private fun onWifiSignalUpdateError(t: Throwable) {
        Toast.makeText(context, "Error $t", Toast.LENGTH_LONG).show()
    }
}
