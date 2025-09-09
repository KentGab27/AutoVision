package com.example.autoestimator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DetectionsAdapter(private val items: List<ResultActivity.ParsedDetection>) :
    RecyclerView.Adapter<DetectionsAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvLabel: TextView = v.findViewById(R.id.tvDetectionLabel)
        val tvConf: TextView = v.findViewById(R.id.tvDetectionConfidence)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detection, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvLabel.text = "${pretty(item.part)} - ${severityName(item.severity)}"
        holder.tvConf.text = "Confidence: ${(item.confidence * 100).toInt()}%"
    }

    override fun getItemCount(): Int = items.size

    private fun pretty(partKey: String): String = when (partKey) {
        "f_bumper" -> "Front Bumper"
        "r_bumper" -> "Rear Bumper"
        "f_windshield" -> "Front Windshield"
        "r_windshield" -> "Rear Windshield"
        "tail_light" -> "Tail Light"
        "side_window" -> "Side Window"
        else -> partKey.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }

    private fun severityName(code: String): String = when (code) {
        "min" -> "Minor"
        "mod" -> "Moderate"
        "sev" -> "Severe"
        else -> code
    }
}
