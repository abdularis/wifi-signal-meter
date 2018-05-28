package com.aar.app.wifinetanalyzer.scanner

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val imageType: ImageView = itemView.findViewById(R.id.imageDevType)
        val textDevType: TextView = itemView.findViewById(R.id.textDevType)

        fun bind(scanResponse: ScanResponse) {
            textIp.text = scanResponse.ip
            textMac.text = scanResponse.mac
            textVendor.text = scanResponse.vendor
            when (scanResponse.type) {
                ScanResponse.DeviceType.Me ->
                    setDeviceTypeIndicator(
                            R.drawable.ic_device_me,
                            R.color.colorAccent,
                            R.string.lbl_me,
                            View.VISIBLE)
                ScanResponse.DeviceType.Router ->
                    setDeviceTypeIndicator(
                            R.drawable.ic_router,
                            R.color.connected_wifi,
                            R.string.lbl_router,
                            View.VISIBLE)
                else ->
                    setDeviceTypeIndicator(
                            R.drawable.ic_device,
                            R.color.colorPrimary)
            }
        }

        private fun setDeviceTypeIndicator(drawableRes: Int,
                                           colorRes: Int,
                                           textRes: Int = 0,
                                           textVisibility: Int = View.GONE) {
            imageType.setImageResource(drawableRes)
            imageType.setColorFilter(imageType.context.resources.getColor(colorRes))
            textDevType.visibility = textVisibility
            if (textRes != 0) textDevType.setText(textRes)
        }
    }
}