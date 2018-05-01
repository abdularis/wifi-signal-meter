package com.github.abdularis.wifisignalmeter.signalmeter

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.abdularis.wifisignalmeter.App

import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.ViewModelFactor
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.signalmeter.aplistdialog.WifiApListDialogFragment
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
        viewModel.wifiUpdates.observe(this, Observer<WifiAccessPointUiModel> { v -> onWifiSignalUpdate(v) })

        buttonSsid.setOnClickListener {
            val ft = activity?.supportFragmentManager?.beginTransaction()
            val dialog = WifiApListDialogFragment()
            dialog.onItemTouchListner = object: WifiApListDialogFragment.OnItemTouchListener {
                override fun onTouch(wifiAp: WifiAccessPoint) {
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

    private fun onWifiSignalUpdate(wifiAp: WifiAccessPointUiModel?) {
        if (wifiAp == null) return
        signalGauge.currentNumber = wifiAp.levelPercent
        textPercent.text = wifiAp.levelPercentText
        textRssi.text = wifiAp.levelRssiText
        textSignalSummary.text = wifiAp.levelText
        textSsid.text = wifiAp.ssid
        textConnection.text = wifiAp.connectionStateText

        if (wifiAp.isConnected) {
            connInfoLayout.visibility = View.VISIBLE

            textWifiName.text = wifiAp.ssid
            textSpeed.text = wifiAp.linkSpeed
            textIp.text = wifiAp.deviceIp
            textMac.text = wifiAp.routerMac
            textFreq.text = wifiAp.frequency
        } else {
            connInfoLayout.visibility = View.GONE
        }
    }

}
