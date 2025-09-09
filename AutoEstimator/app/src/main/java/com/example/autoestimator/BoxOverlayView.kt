package com.example.autoestimator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BoxOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val boxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.RED
        alpha = 0
    }

    private val textPaint = Paint().apply {
        color = Color.YELLOW
        textSize = 32f
        style = Paint.Style.FILL
        typeface = Typeface.DEFAULT_BOLD
        alpha = 0
    }

    private var detections: List<DetectionResult> = emptyList()

    // The original model input size (must match DamageClassifier inputSize)
    private val modelInputSize = 640f

    // Keep track of the bitmap size you drew inside the ImageView
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0


    fun setImageInfo(width: Int, height: Int) {
        imageWidth = width
        imageHeight = height
        invalidate()
    }


    fun setDetections(results: List<DetectionResult>) {
        detections = results
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (detections.isEmpty() || imageWidth == 0 || imageHeight == 0) return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Compute scale to fit image inside view (FIT_CENTER logic)
        val scale = minOf(viewWidth / imageWidth, viewHeight / imageHeight)
        val dx = (viewWidth - imageWidth * scale) / 2f
        val dy = (viewHeight - imageHeight * scale) / 2f

        for (det in detections) {
            val box = det.box

            val left = box[1] * imageWidth * scale + dx
            val top = box[0] * imageHeight * scale + dy
            val right = box[3] * imageWidth * scale + dx
            val bottom = box[2] * imageHeight * scale + dy

            // Draw box
            canvas.drawRect(left, top, right, bottom, boxPaint)

            // Draw label + confidence
            val labelText = "${det.label} ${(det.confidence * 100).toInt()}%"
            canvas.drawText(labelText, left, top - 10, textPaint)
        }
    }
}
