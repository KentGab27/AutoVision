package com.example.autoestimator

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ResultActivity : AppCompatActivity() {

    // Parsed version of DetectionResult for displaying part + severity
    data class ParsedDetection(
        val part: String,
        val severity: String,
        val confidence: Float
    )

    // Cost row for cost adapter
    data class CostRow(
        val label: String,
        val autoshopPrice: Int,
        val insurancePrice: Int,
        val confidence: Float
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val img = findViewById<ImageView>(R.id.imgResult)
        val overlay = findViewById<BoxOverlayView>(R.id.resultOverlay)

        var displayBitmap: Bitmap? = null

        // Load image from gallery
        intent.getStringExtra("imageUri")?.let { uriStr ->
            val uri = Uri.parse(uriStr)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            displayBitmap = resizeBitmap(bitmap, 640)
            img.setImageBitmap(displayBitmap)
        }

        // Load image from camera
        intent.getParcelableExtra<Bitmap>("capturedBitmap")?.let { bitmap ->
            displayBitmap = resizeBitmap(bitmap, 640)
            img.setImageBitmap(displayBitmap)
        }

        // Load detection results (with boxes)
        val detectionsJson = intent.getStringExtra("detectionResults") ?: "[]"
        val listType = object : TypeToken<List<DetectionResult>>() {}.type
        val rawDetections: List<DetectionResult> = Gson().fromJson(detectionsJson, listType)


        val topDetections = rawDetections.take(3)

        // Sync overlay with the actual displayed ImageView size
        img.post {
            overlay.setImageInfo(img.width, img.height)
            overlay.setDetections(topDetections)
        }

        // Convert raw detections into parsed ones for RecyclerView
        val parsedDetections = topDetections.mapNotNull { toParsedDetection(it) }

        val rvDet = findViewById<RecyclerView>(R.id.rvDetections)
        rvDet.layoutManager = LinearLayoutManager(this)
        rvDet.adapter = DetectionsAdapter(parsedDetections)

        // Cost rows
        val costs = parsedDetections.map { det ->
            val key = "${det.part.lowercase()}_${det.severity.lowercase()}"
            val pricePair = PriceData.prices[key]

            val autoshop = pricePair?.first ?: -1
            val insurance = pricePair?.second ?: -1

            val friendlyLabel = PriceData.labelNames[key]
                ?: "${pretty(det.part)} - ${severityName(det.severity)}"

            CostRow(
                friendlyLabel,
                autoshop,
                insurance,
                det.confidence
            )
        }

        val rvCost = findViewById<RecyclerView>(R.id.rvCosts)
        rvCost.layoutManager = LinearLayoutManager(this)
        rvCost.adapter = CostAdapter(costs)

        // Totals
        val autoTotal = costs.sumOf { if (it.autoshopPrice >= 0) it.autoshopPrice else 0 }
        val insTotal = costs.sumOf { if (it.insurancePrice >= 0) it.insurancePrice else 0 }

        findViewById<TextView>(R.id.tvTotal).text =
            "Autoshop: ₱%,d | Insurance: ₱%,d".format(autoTotal, insTotal)

        // Buttons
        findViewById<ImageButton>(R.id.btnResultBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            startActivity(
                Intent(this, WelcomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }

    private fun resizeBitmap(source: Bitmap, size: Int): Bitmap {
        return Bitmap.createScaledBitmap(source, size, size, true)
    }

    // Convert DetectionResult → ParsedDetection
    private fun toParsedDetection(det: DetectionResult): ParsedDetection? {
        val idx = det.label.lastIndexOf('_')
        if (idx == -1) return null
        val part = det.label.substring(0, idx)
        val sev = det.label.substring(idx + 1)
        return ParsedDetection(part, sev, det.confidence)
    }

    private fun severityName(code: String): String = when (code) {
        "min" -> "Minor"
        "mod" -> "Moderate"
        "sev" -> "Severe"
        else -> code
    }

    private fun pretty(partKey: String): String = when (partKey) {
        "f_bumper" -> "Front Bumper"
        "r_bumper" -> "Rear Bumper"
        "f_windshield" -> "Front Windshield"
        "r_windshield" -> "Rear Windshield"
        "tail_light" -> "Tail Light"
        "side_window" -> "Side Window"
        else -> partKey.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }
}
