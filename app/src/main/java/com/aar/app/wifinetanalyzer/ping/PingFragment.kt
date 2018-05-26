package com.aar.app.wifinetanalyzer.ping

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.SeekBar
import com.aar.app.wifinetanalyzer.App
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_ping.*
import javax.inject.Inject

class PingFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: PingViewModel
    private lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = ArrayAdapter(activity, R.layout.item_ping_result, R.id.textPingResult)
        listView.adapter = listAdapter

        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PingViewModel::class.java)

        viewModel.pingResults.observe(this, Observer {
            it?.let { onPingResultChanged(it) }
        })

        viewModel.state.observe(this, Observer {
            it?.let { onStateChanged(it) }
        })

        initView()
    }

    private fun onPingResultChanged(results: List<PingResponse>) {
        val first = results.first()
        textIp.text = first.ip ?: resources.getString(R.string.empty_ip)
        textMac.text = first.mac ?: resources.getString(R.string.default_mac)
        textVendor.text = first.vendor ?: resources.getString(R.string.err_na)

        listAdapter.clear()
        listAdapter.addAll(
                results.mapIndexed { index, pingResponse ->
                    val times = index + 1
                    if (!pingResponse.isReachable) "$times - HOST NOT REACHABLE"
                    else "$times - from:  ${pingResponse.ip}:  ${pingResponse.timeTaken} ms"
                }
        )
        listView.setSelection(listAdapter.count - 1)
    }

    private fun onStateChanged(state: PingViewModel.State) {
        layoutResult.visibility = View.VISIBLE
        textMessage.visibility = View.GONE

        if (state == PingViewModel.State.PING_STARTED) {
            listAdapter.clear()
            textIp.setText(R.string.empty_ip)
            textMac.setText(R.string.default_mac)
            textVendor.setText(R.string.err_na)
            notifStarted()
        } else if (state == PingViewModel.State.PING_COMPLETED) {
            notifComplete()
        } else if (state == PingViewModel.State.PING_STOPPED) {
            notifStopped()
        } else {
            layoutResult.visibility = View.GONE
            textMessage.visibility = View.VISIBLE
            buttonPing.text = getString(R.string.lbl_ping)

            if (state == PingViewModel.State.PING_ERROR) {
                textMessage.text = getString(R.string.err_ping_msg)
            } else if (state == PingViewModel.State.IP_ERROR) {
                textMessage.text = getString(R.string.err_ip_address_msg)
            }

        }
    }

    private fun notifComplete() {
        textNotif.setTextColor(resources.getColor(R.color.ping_completed))
        textNotif.text = getString(R.string.lbl_completed)
        buttonPing.text = getString(R.string.lbl_ping)
        buttonPing.setBackgroundResource(R.drawable.bg_ping_btn)
    }

    private fun notifStopped() {
        textNotif.setTextColor(resources.getColor(R.color.ping_stopped))
        textNotif.text = getString(R.string.lbl_stopped)
        buttonPing.text = getString(R.string.lbl_ping)
        buttonPing.setBackgroundResource(R.drawable.bg_ping_btn)
    }

    private fun notifStarted() {
        textNotif.setTextColor(resources.getColor(R.color.ping_started))
        textNotif.text = getString(R.string.lbl_in_progress)
        buttonPing.text = getString(R.string.lbl_stop)
        buttonPing.setBackgroundResource(R.drawable.bg_ping_btn_stop)
    }

    private fun seekBarCountProgress() = if (seekBarCount.progress == 0) 1 else seekBarCount.progress

    private fun initView() {
        buttonPing.setOnClickListener {
            val inputMgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromInputMethod(textIp.windowToken, 0)

            if (viewModel.pingInProgress) {
                viewModel.stopPing()
            } else {
                val count = seekBarCountProgress()
                viewModel.ping(textEditIp.text.toString(), count)
            }
        }

        seekBarCount.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textTimes.text = seekBarCountProgress().toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        seekBarCount.progress = viewModel.currentPingCount
        textTimes.text = seekBarCountProgress().toString()
        textEditIp.setText(viewModel.currentIp)
    }
}