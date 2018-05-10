package com.aar.app.wifinetanalyzer.signalmeter

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.wifi.WifiInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aar.app.wifinetanalyzer.App
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.common.calcSignalPercentage
import com.aar.app.wifinetanalyzer.common.percentToSignalLevel
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint
import com.aar.app.wifinetanalyzer.wifiselector.WifiSelectorActivity
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
        viewModel.startSignalUpdate()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopSignalUpdate()
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
            textSignalSummary.text = percentToSignalLevel(context?.resources, levelPercent)
            textSsid.text = if (wifiAp.signal.isHidden) getString(R.string.hidden_wifi) else wifiAp.signal.ssid
            textBssid.text = wifiAp.signal.bssid
            textManufacture.text = wifiAp.signal.vendor
            textFreq.text = wifiAp.signal.channel.frequency.toString()
            textChannel.text = wifiAp.signal.channel.channelNumber.toString()
            textCapabilities.text = wifiAp.signal.capabilities
            textConnection.setText(if (wifiAp.isConnected) R.string.text_connected else R.string.text_not_connected)
            imageLock.visibility = if (wifiAp.signal.authenticationNeeded) View.VISIBLE else View.GONE
            imageHidden.visibility = if (wifiAp.signal.isHidden) View.VISIBLE else View.GONE

            if (wifiAp.isConnected) {
                val connInfo = wifiAp.connectionInfo

                connInfoLayout.visibility = View.VISIBLE
                imageConnected.visibility = View.VISIBLE
                textWifiName.text = wifiAp.signal.ssid
                textSpeed.text = "%d %s".format(connInfo?.linkSpeed, WifiInfo.LINK_SPEED_UNITS)
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
            textSignalSummary.setText(R.string.no_signal)
            textSsid.text = getString(R.string.select_wifi)
            textBssid.text = getString(R.string.default_mac)
            textManufacture.text = "-"
            textConnection.text = getString(R.string.cannot_detect)
            textFreq.text = "0"
            textChannel.text = "0"
            textCapabilities.text = "[]"
            connInfoLayout.visibility = View.GONE
            imageLock.visibility = View.GONE
            imageHidden.visibility = View.GONE
        }
    }

}
