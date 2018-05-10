package com.github.abdularis.wifisignalmeter.wifilist

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        var textVendor: TextView = itemView.findViewById(R.id.textVendor)
        var textFreq: TextView = itemView.findViewById(R.id.textFreq)
        var textChannel: TextView = itemView.findViewById(R.id.textChannel)
        var imageLock: ImageView = itemView.findViewById(R.id.imageLock)
        var itemContainer: View = itemView.findViewById(R.id.itemContainer)

        init {
            signalGauge.enableAnimation = false
        }

        fun bind(onItemClickListener: OnItemClickListener?, wifiAp: WifiAccessPoint) {
            val levelPercent = calcSignalPercentage(wifiAp.signal.level)
            if (wifiAp.signal.isHidden) {
                textSsid.setTextColor(ContextCompat.getColor(textSsid.context, R.color.hidden_wifi))
                textSsid.text = "<hidden wifi>"
            } else {
                textSsid.setTextColor(ContextCompat.getColor(textSsid.context, R.color.item_text_primary))
                textSsid.text = wifiAp.signal.ssid
            }
            if (wifiAp.isConnected) {
                textSsid.setTextColor(ContextCompat.getColor(textSsid.context, R.color.connected_wifi))
            }
            imageLock.visibility = if (wifiAp.signal.authenticationNeeded) View.VISIBLE else View.GONE
            textBssid.text = wifiAp.signal.bssid
            signalGauge.currentNumber = levelPercent
            textRssi.text = wifiAp.signal.level.toString()
            textVendor.text = wifiAp.signal.vendor
            textFreq.text = wifiAp.signal.channel.frequency.toString()
            textChannel.text = wifiAp.signal.channel.channelNumber.toString()
            itemContainer.setOnClickListener { v ->
                onItemClickListener?.onItemClick(wifiAp)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(wifiAp: WifiAccessPoint)
    }

}