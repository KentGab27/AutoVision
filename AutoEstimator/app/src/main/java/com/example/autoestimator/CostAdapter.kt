package com.example.autoestimator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CostAdapter(private val items: List<ResultActivity.CostRow>) :
    RecyclerView.Adapter<CostAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvLabel: TextView = v.findViewById(R.id.tvCostLabel)
        val tvAuto: TextView = v.findViewById(R.id.tvCostAuto)
        val tvInsurance: TextView = v.findViewById(R.id.tvCostInsurance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cost, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]


        holder.tvLabel.text = PriceData.labelNames[item.label] ?: item.label

        holder.tvAuto.text = "₱%,d".format(item.autoshopPrice)

        holder.tvInsurance.text = if (item.insurancePrice >= 0) {
            "₱%,d".format(item.insurancePrice)
        } else {
            "N/A"
        }
    }

    override fun getItemCount(): Int = items.size
}
