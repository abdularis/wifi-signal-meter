package com.aar.app.wifinetanalyzer.scanner

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aar.app.wifinetanalyzer.R

class DeviceListAdapter: RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    private val data: ArrayList<ScanResponse> = ArrayList()

    fun insert(scanResponse: ScanResponse) {
        data.add(scanResponse)
        notifyItemInserted(data.size - 1)
    }

    fun replaceAll(scanResponses: Collection<ScanResponse>) {
        data.clear()
        data.addAll(scanResponses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textIp: TextView = itemView.findViewById(R.id.textIp)
        val textMac: TextView = itemView.findViewById(R.id.textMac)
        val textVendor: TextView = itemView.findViewById(R.id.textVendor)

        fun bind(scanResponse: ScanResponse) {
            textIp.text = scanResponse.ip
            textMac.text = scanResponse.mac
            textVendor.text = scanResponse.vendor
        }
    }
}