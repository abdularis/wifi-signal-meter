package com.github.abdularis.wifisignalmeter.signalmeter

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.abdularis.wifisignalmeter.App

import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.ViewModelFactory
import com.github.abdularis.wifisignalmeter.common.calcSignalPercentage
import com.github.abdularis.wifisignalmeter.common.percentToSignalLevel
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint
import com.github.abdularis.wifisignalmeter.wifiselector.WifiSelectorActivity
import kotlinx.android.synthetic.main.fragment_signal_meter.*
import javax.inject.Inject


class SignalMeterFragment : Fragment() {

    @Inject lateinit var mViewModelFactory: ViewModelFactory
    lateinit var viewModel: SignalMeterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signal_meter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(SignalMeterViewModel::class.java)
        viewModel.wifiUpdates.observe(this, Observer<WifiAccessPoint> { v -> onWifiSignalUpdate(v) })

        buttonSsid.setOnClickListener {
            val i = Intent(context, WifiSelectorActivity::class.java)
            startActivityForResult(i, 110)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 110) {
            val ssid = data?.extras?.get(WifiSelectorActivity.EXTRA_SSID).toString()
            val bssid = data?.extras?.get(WifiSelectorActivity.EXTRA_BSSID).toString()
            viewModel.filter(ssid, bssid)
        }
    }

    private fun onWifiSignalUpdate(wifiAp: WifiAccessPoint?) {
        if (wifiAp != null) {
            val levelPercent = calcSignalPercentage(wifiAp.signal.level)

            signalGauge.currentNumber = levelPercent
            textPercent.text = "$levelPercent"
            textRssi.text = "${wifiAp.signal.level}"
            textSignalSummary.text = percentToSignalLevel(levelPercent)
            textSsid.text = if (wifiAp.signal.isHidden) "<hidden wifi>" else wifiAp.signal.ssid
            textBssid.text = wifiAp.signal.bssid
            textManufacture.text = wifiAp.signal.vendor
            textFreq.text = "${wifiAp.signal.channel.frequency} MHz,  Ch: ${wifiAp.signal.channel.channelNumber}"
            textCapabilities.text = wifiAp.signal.capabilities
            textConnection.text = if (wifiAp.isConnected) "Connected" else "Not connected"
            imageLock.visibility = if (wifiAp.signal.authenticationNeeded) View.VISIBLE else View.GONE
            imageHidden.visibility = if (wifiAp.signal.isHidden) View.VISIBLE else View.GONE

            if (wifiAp.isConnected) {
                val connInfo = wifiAp.connectionInfo

                connInfoLayout.visibility = View.VISIBLE
                imageConnected.visibility = View.VISIBLE
                textWifiName.text = wifiAp.signal.ssid
                textSpeed.text = "${connInfo?.linkSpeed} Mbps"
                textIp.text = connInfo?.ipAddress
                textDns1.text = connInfo?.dns1
                textDns2.text = connInfo?.dns2
                textGateway.text = connInfo?.gateway
                textNetmask.text = connInfo?.netmask
                textServerAddress.text = connInfo?.serverAddress
            } else {
                connInfoLayout.visibility = View.GONE
                imageConnected.visibility = View.GONE
            }
        } else {
            signalGauge.currentNumber = 0
            textPercent.text = "0"
            textRssi.text = "0"
            textSignalSummary.text = "No Signal"
            textSsid.text = "<select wifi>"
            textBssid.text = "02:00:00:00:00:00"
            textManufacture.text = "-"
            textConnection.text = "Cannot detect wifi signal"
            textFreq.text = "- MHz,  Ch: -"
            textCapabilities.text = "[]"
            connInfoLayout.visibility = View.GONE
            imageLock.visibility = View.GONE
            imageHidden.visibility = View.GONE
        }
    }

}
