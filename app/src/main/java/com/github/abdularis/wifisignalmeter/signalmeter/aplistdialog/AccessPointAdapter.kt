package com.github.abdularis.wifisignalmeter.signalmeter.aplistdialog

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.abdularis.wifisignalmeter.R
import com.github.abdularis.wifisignalmeter.model.WifiAccessPoint

class AccessPointAdapter : RecyclerView.Adapter<AccessPointAdapter.ViewHolder>() {

    var onTouchListener: OnTouchListener? = null
    var wifiAp: List<WifiAccessPoint> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_wifi_ap, parent, false))

    override fun getItemCount(): Int = wifiAp.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(onTouchListener, wifiAp[position])

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textSsid: TextView = itemView.findViewById(R.id.textSsid)
        var textBssid: TextView = itemView.findViewById(R.id.textBssid)
        var itemContainer: View = itemView.findViewById(R.id.itemContainer)

        fun bind(onTouchListener: OnTouchListener?, wifiAp: WifiAccessPoint) {
            textSsid.text = wifiAp.ssid
            textBssid.text = wifiAp.bssid
            itemContainer.setOnTouchListener { v, event ->
                onTouchListener?.onTouch(wifiAp)
                true
            }
        }
    }

    interface OnTouchListener {
        fun onTouch(wifiAp: WifiAccessPoint)
    }
}