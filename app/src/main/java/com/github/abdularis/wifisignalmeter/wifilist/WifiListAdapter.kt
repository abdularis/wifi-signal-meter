package com.github.abdularis.wifisignalmeter.wifilist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.abdularis.gaugeview.LinearGaugeView
import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.common.calcSignalPercentage
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint

class WifiListAdapter: RecyclerView.Adapter<WifiListAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var wifiAp: List<WifiAccessPoint> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_wifi, parent, false))

    override fun getItemCount(): Int = wifiAp.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(onItemClickListener, wifiAp[position])

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textSsid: TextView = itemView.findViewById(R.id.textSsid)
        var textBssid: TextView = itemView.findViewById(R.id.textBssid)
        var signalGauge: LinearGaugeView = itemView.findViewById(R.id.signalGauge)
        var textRssi: TextView = itemView.findViewById(R.id.textRssi)
        var textFreq: TextView = itemView.findViewById(R.id.textFreq)
        var itemContainer: View = itemView.findViewById(R.id.itemContainer)

        fun bind(onItemClickListener: OnItemClickListener?, wifiAp: WifiAccessPoint) {
            val levelPercent = calcSignalPercentage(wifiAp.signal.level)
            textSsid.text = wifiAp.signal.ssid
            textBssid.text = wifiAp.signal.bssid
            signalGauge.currentNumber = levelPercent
            textRssi.text = wifiAp.signal.level.toString()
            textFreq.text = wifiAp.signal.channel.frequency.toString()
            itemContainer.setOnClickListener { v ->
                onItemClickListener?.onItemClick(wifiAp)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(wifiAp: WifiAccessPoint)
    }

}