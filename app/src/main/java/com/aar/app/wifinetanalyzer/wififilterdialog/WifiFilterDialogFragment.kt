package com.aar.app.wifinetanalyzer.wififilterdialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Spinner
import android.widget.TextView
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.filter.ChannelFilter
import com.aar.app.wifinetanalyzer.filter.CompositeFilter
import com.aar.app.wifinetanalyzer.filter.SsidFilter

class WifiFilterDialogFragment: DialogFragment() {

    var onFilterListener: ((filters: CompositeFilter) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
                .setTitle(R.string.lbl_filter_by)
                .setView(R.layout.fragment_wifi_filter_dialog)
                .setPositiveButton(R.string.lbl_filter, { _, _ -> onPositiveButton() })
                .setNegativeButton(R.string.lbl_cancel, { _, _ -> /** empty **/ })
                .create()
    }

    private fun onPositiveButton() {
        val textSsid = dialog.findViewById<TextView>(R.id.textSsid)
        val spinnerChannel = dialog.findViewById<Spinner>(R.id.spinnerChannel)

        val ssid = textSsid.text.toString()
        val channel = spinnerChannel.selectedItem.toString()

        val filters = CompositeFilter()
        if (!ssid.isEmpty()) filters.add(SsidFilter(ssid))
        if (!channel.isEmpty()) filters.add(ChannelFilter(channel.toInt()))
        onFilterListener?.invoke(filters)
    }

}