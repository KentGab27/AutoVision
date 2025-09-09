package com.example.autoestimator

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max
import kotlin.math.min

class DamageClassifier(private val context: Context) {

    private var interpreter: Interpreter
    private var labels: List<String>
    private val inputSize = 640

    init {
        interpreter = Interpreter(loadModelFile("carDamage2.tflite"))
        labels = context.assets.open("labels.txt").bufferedReader().readLines()
    }

    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    fun detectDamage(
        bitmap: Bitmap,
        confidenceThreshold: Float = 0.5f,
        iouThreshold: Float = 0.45f
    ): List<DetectionResult> {

        val origWidth = bitmap.width
        val origHeight = bitmap.height

        val safeBitmap = makeSafeBitmap(bitmap)
        val scaledBitmap = Bitmap.createScaledBitmap(safeBitmap, inputSize, inputSize, true)
        val inputBuffer = convertBitmapToByteBuffer(scaledBitmap, inputSize)

        // Dynamically check output shape
        val outputShape = interpreter.getOutputTensor(0).shape()

        val numBoxes: Int
        val numClasses: Int
        val isTransposed: Boolean

        if (outputShape.size == 3 && outputShape[1] > outputShape[2]) {
            numBoxes = outputShape[1]
            numClasses = outputShape[2] - 4
            isTransposed = false
        } else {
            numBoxes = outputShape[2]
            numClasses = outputShape[1] - 4
            isTransposed = true
        }

        val candidates = mutableListOf<DetectionResult>()

        if (!isTransposed) {
            val outputArray = Array(1) { Array(numBoxes) { FloatArray(numClasses + 4) } }
            interpreter.run(inputBuffer, outputArray)
            for (i in 0 until numBoxes) {
                processBox(outputArray[0][i], numClasses, confidenceThreshold, candidates, origWidth, origHeight)
            }
        } else {
            val rawOutput = Array(1) { Array(numClasses + 4) { FloatArray(numBoxes) } }
            interpreter.run(inputBuffer, rawOutput)
            for (b in 0 until numBoxes) {
                val boxData = FloatArray(numClasses + 4)
                for (c in 0 until numClasses + 4) {
                    boxData[c] = rawOutput[0][c][b]
                }
                processBox(boxData, numClasses, confidenceThreshold, candidates, origWidth, origHeight)
            }
        }

        return nonMaxSuppression(candidates, iouThreshold).take(3)
    }

    private fun processBox(
        boxData: FloatArray,
        numClasses: Int,
        confidenceThreshold: Float,
        candidates: MutableList<DetectionResult>,
        origWidth: Int,
        origHeight: Int
    ) {
        val cx = boxData[0]
        val cy = boxData[1]
        val w = boxData[2]
        val h = boxData[3]
        val scores = boxData.copyOfRange(4, 4 + numClasses)

        // Find best class
        var bestClass = -1
        var bestScore = 0f
        for (c in scores.indices) {
            val score = scores[c]
            if (score > bestScore) {
                bestScore = score
                bestClass = c
            }
        }

        if (bestScore >= confidenceThreshold) {
            val label = labels.getOrNull(bestClass) ?: "Unknown"

            val xminN = (cx - w / 2) / inputSize
            val yminN = (cy - h / 2) / inputSize
            val xmaxN = (cx + w / 2) / inputSize
            val ymaxN = (cy + h / 2) / inputSize

            val xmin = xminN * origWidth
            val ymin = yminN * origHeight
            val xmax = xmaxN * origWidth
            val ymax = ymaxN * origHeight

            candidates.add(
                DetectionResult(label, bestScore, listOf(xmin, ymin, xmax, ymax))
            )
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap, inputSize: Int): ByteBuffer {
        val safeBitmap = makeSafeBitmap(bitmap)

        val buffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        buffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        safeBitmap.getPixels(intValues, 0, safeBitmap.width, 0, 0, safeBitmap.width, safeBitmap.height)

        var pixelIndex = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixel = intValues[pixelIndex++]
                buffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f))
                buffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f))
                buffer.putFloat(((pixel and 0xFF) / 255.0f))
            }
        }
        buffer.rewind()
        return buffer
    }

    private fun nonMaxSuppression(
        detections: List<DetectionResult>,
        iouThreshold: Float
    ): List<DetectionResult> {
        val results = mutableListOf<DetectionResult>()
        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()

        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            results.add(best)

            val it = sorted.iterator()
            while (it.hasNext()) {
                val other = it.next()
                if (iou(best.box, other.box) > iouThreshold && best.label == other.label) {
                    it.remove()
                }
            }
        }
        return results
    }

    private fun iou(box1: List<Float>, box2: List<Float>): Float {
        val x1 = max(box1[0], box2[0])
        val y1 = max(box1[1], box2[1])
        val x2 = min(box1[2], box2[2])
        val y2 = min(box1[3], box2[3])

        val interArea = max(0f, x2 - x1) * max(0f, y2 - y1)
        val box1Area = (box1[2] - box1[0]) * (box1[3] - box1[1])
        val box2Area = (box2[2] - box2[0]) * (box2[3] - box2[1])

        return interArea / (box1Area + box2Area - interArea + 1e-6f)
    }

    private fun makeSafeBitmap(bitmap: Bitmap): Bitmap {
        return if (bitmap.config != Bitmap.Config.ARGB_8888 || !bitmap.isMutable) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }
    }
}
