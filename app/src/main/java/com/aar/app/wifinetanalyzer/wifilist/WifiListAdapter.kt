package com.aar.app.wifinetanalyzer.wifilist

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.abdularis.gaugeview.LinearGaugeView
import com.aar.app.wifinetanalyzer.R
import com.aar.app.wifinetanalyzer.common.calcSignalPercentage
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint

class WifiListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM_WIFI_ACCESS_POINT = 1
        private const val TYPE_ITEM_HEADER = 2
    }

    var onItemClickListener: OnItemClickListener? = null
    private val data: ArrayList<Any> = ArrayList()

    private fun addConnectedWifiAp(wifiAp: WifiAccessPoint) {
        data.add(WifiItemHeader(R.string.connected_wifi))
        data.add(wifiAp)
    }

    private fun addInterferingWifiAps(wifiAps: List<WifiAccessPoint>) {
        data.add(WifiItemHeader(R.string.interfering_channel))
        data.addAll(wifiAps)
    }

    private fun addNonInterferingWifiAps(wifiAps: List<WifiAccessPoint>) {
        data.add(WifiItemHeader(R.string.non_interfering_channel))
        data.addAll(wifiAps)
    }

    fun replaceData(wifiAps: List<WifiAccessPoint>) {
        data.clear()
        data.addAll(wifiAps)
        notifyDataSetChanged()
    }

    fun replaceData(wifiAccessPointList: WifiAccessPointList) {
        data.clear()
        if (wifiAccessPointList.connectedWifi == null) {
            data.add(WifiItemHeader(R.string.text_not_connected))
            data.addAll(wifiAccessPointList.wifiList)
        } else {
            wifiAccessPointList.connectedWifi.let { addConnectedWifiAp(it) }
            addInterferingWifiAps(wifiAccessPointList.interferingWifiList)
            addNonInterferingWifiAps(wifiAccessPointList.otherWifiList)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_ITEM_WIFI_ACCESS_POINT) {
            WifiApViewHolder(inflater.inflate(R.layout.item_wifi, parent, false))
        } else {
            WifiItemHeaderViewHolder(inflater.inflate(R.layout.item_wifi_header, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_ITEM_WIFI_ACCESS_POINT) {
            (holder as WifiApViewHolder).bind(onItemClickListener, data[position] as WifiAccessPoint)
        } else if (holder.itemViewType == TYPE_ITEM_HEADER) {
            (holder as WifiItemHeaderViewHolder).bind(data[position] as WifiItemHeader)
        }
    }

    override fun getItemViewType(position: Int): Int = when (data[position]) {
        is WifiAccessPoint -> TYPE_ITEM_WIFI_ACCESS_POINT
        else -> TYPE_ITEM_HEADER
    }

    data class WifiItemHeader(val messageStringRes: Int)

    class WifiItemHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.textTitle)
        fun bind(header: WifiItemHeader) {
            title.setText(header.messageStringRes)
        }
    }

    class WifiApViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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