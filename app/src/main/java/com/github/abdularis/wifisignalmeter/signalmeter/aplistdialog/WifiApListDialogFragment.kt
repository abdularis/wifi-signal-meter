package com.github.abdularis.wifisignalmeter.signalmeter.aplistdialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.abdularis.wifisignalmeter.App

import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.ViewModelFactor
import com.github.abdularis.wifisignalmeter.model.WifiAp
import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_wifi_ap_list_dialog.*
import javax.inject.Inject


class WifiApListDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactor: ViewModelFactor
    lateinit var viewModel: SignalMeterViewModel
    private val apAdapter: ApAdapter = ApAdapter()

    var onItemTouchListner: OnItemTouchListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactor).get(SignalMeterViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wifi_ap_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apAdapter.onTouchListener = object: ApAdapter.OnTouchListener {
            override fun onTouch(wifiAp: WifiAp) {
                onItemTouchListner?.onTouch(wifiAp)
                dismiss()
            }
        }
        recyclerView.adapter = apAdapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewModel.wifiList.observe(this, Observer<List<WifiAp>?> { v -> v?.let{ apAdapter.wifiAp = it } })
    }

    interface OnItemTouchListener {
        fun onTouch(wifiAp: WifiAp)
    }
}
