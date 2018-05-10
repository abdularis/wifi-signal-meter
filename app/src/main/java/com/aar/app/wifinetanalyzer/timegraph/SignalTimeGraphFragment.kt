package com.aar.app.wifinetanalyzer.timegraph


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aar.app.wifinetanalyzer.App
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.ViewModelFactory
import com.aar.app.wifinetanalyzer.wifiselector.WifiSelectorActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_signal_time_graph.*
import javax.inject.Inject


class SignalTimeGraphFragment : Fragment() {

    companion object {
        const val MAX_X_AXIS = 30f
        const val MIN_Y_AXIS = -20f
        const val MAX_Y_AXIS = -100f
        const val Y_AXIS_GRANULARITY = 10f

        const val REQ_CODE = 110
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory
    private lateinit var viewModel: SignalTimeGraphViewModel
    private lateinit var lineDataSet: LineDataSet

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signal_time_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGraph()

        buttonSsid.setOnClickListener {
            val i = Intent(context, WifiSelectorActivity::class.java)
            startActivityForResult(i, REQ_CODE)
        }

        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, mViewModelFactory).get(SignalTimeGraphViewModel::class.java)
        viewModel.signalshotBuffer.observe(this, Observer {
            it?.let {
                textSsid.text = it.wifiAp?.signal?.ssid ?: getString(R.string.select_wifi)
                textBssid.text = it.wifiAp?.signal?.bssid ?: getString(R.string.default_mac)
                updateGraph(it.data)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE) {
            val ssid = data?.extras?.get(WifiSelectorActivity.EXTRA_SSID).toString()
            val bssid = data?.extras?.get(WifiSelectorActivity.EXTRA_BSSID).toString()
            viewModel.filter(ssid, bssid)
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

    private fun updateGraph(data: Collection<Float>) {
        var x = 0f
        lineDataSet.clear()
        for (y in data) {
            lineDataSet.addEntry(Entry(x++, y))
        }

        lineDataSet.notifyDataSetChanged()
        chart.invalidate()
    }

    private fun initGraph() {
        val entries = ArrayList<Entry>()

        // workaround: if the list is empty the MPAndroidChart lib will
        // throw an IndexOutOfBoundsException (bug)
        entries.add(Entry(0f, 0f))

        lineDataSet = LineDataSet(entries, getString(R.string.signal_strength_label))
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.color = Color.WHITE
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        chart.setTouchEnabled(false)
        chart.description.isEnabled = false
        chart.legend.textColor = Color.WHITE
        chart.data = LineData(lineDataSet)

        val xAxis = chart.xAxis
        val axisRight = chart.axisRight
        val axisLeft = chart.axisLeft

        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE
        xAxis.textColor = Color.WHITE
        xAxis.axisMaximum = MAX_X_AXIS
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        axisRight.isEnabled = false

        axisLeft.axisLineColor = Color.WHITE
        axisLeft.gridColor = Color.WHITE
        axisLeft.textColor = Color.WHITE
        axisLeft.axisMaximum = MIN_Y_AXIS
        axisLeft.axisMinimum = MAX_Y_AXIS
        axisLeft.granularity = Y_AXIS_GRANULARITY
        axisLeft.enableGridDashedLine(3f, 2f, 0f)
    }
}
