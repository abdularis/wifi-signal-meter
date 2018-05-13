package com.aar.app.wifinetanalyzer.ouilookup

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aar.app.wifinetanalyzer.R

class OuiListAdapter: RecyclerView.Adapter<OuiListAdapter.ViewHolder>() {

    var data: List<OuiLookupResult> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_oui, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textMac: TextView = itemView.findViewById(R.id.textMac)
        val textVendor: TextView = itemView.findViewById(R.id.textVendor)
        fun bind(lookupResult: OuiLookupResult) {
            textMac.text = lookupResult.mac
            textVendor.text = lookupResult.vendor
        }
    }

}