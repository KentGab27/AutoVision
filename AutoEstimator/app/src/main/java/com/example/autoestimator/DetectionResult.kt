package com.example.autoestimator

/** Shared detection model for classifier, overlay, and result screen */
data class DetectionResult(
    val label: String,
    val confidence: Float,
    val box: List<Float>
)
