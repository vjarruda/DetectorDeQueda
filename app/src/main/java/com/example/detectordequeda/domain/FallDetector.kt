package com.example.detectordequeda.domain

import com.example.detectordequeda.domain.model.FallClassification
import com.example.detectordequeda.domain.model.FallResult
import com.example.detectordequeda.domain.model.Landmark
import com.example.detectordequeda.domain.model.PoseAnalysis
import kotlin.math.abs
import kotlin.math.atan2

object FallDetector {
    // ângulo (em graus, em relação à vertical) para determinar a postura
    private const val THRESHOLD_ATTENTION = 45f
    private const val THRESHOLD_FALL = 65f

    // índices do MediaPipe Pose
    private const val LEFT_SHOULDER = 11
    private const val RIGHT_SHOULDER = 12
    private const val LEFT_HIP = 23
    private const val RIGHT_HIP = 24
    private const val MIN_LANDMARKS = RIGHT_HIP + 1

    fun analyze(landmarks: List<Landmark>, aspectRatio: Float = 1f): PoseAnalysis? {
        if (landmarks.size < MIN_LANDMARKS) return null

        val shoulderMid = midpoint(landmarks[LEFT_SHOULDER], landmarks[RIGHT_SHOULDER])
        val hipMid = midpoint(landmarks[LEFT_HIP], landmarks[RIGHT_HIP])

        return PoseAnalysis(
            landmarks = landmarks,
            shoulderMid = shoulderMid,
            hipMid = hipMid,
            result = classify(shoulderMid, hipMid, aspectRatio)
        )
    }

    private fun classify(shoulderMid: Landmark, hipMid: Landmark, aspectRatio: Float): FallResult {
        // x é normalizado pela largura e y pela altura: reescala x para ficar na mesma unidade de y
        val dx = (hipMid.x - shoulderMid.x) * aspectRatio
        val dy = hipMid.y - shoulderMid.y

        val angleDeg = Math.toDegrees(atan2(abs(dx.toDouble()), abs(dy.toDouble()))).toFloat()

        val classification = when {
            angleDeg > THRESHOLD_FALL -> FallClassification.POSSIBLE_FALL
            angleDeg > THRESHOLD_ATTENTION -> FallClassification.ATTENTION
            else -> FallClassification.NORMAL
        }
        return FallResult(angleDeg, classification)
    }

    private fun midpoint(a: Landmark, b: Landmark) =
        Landmark((a.x + b.x) / 2f, (a.y + b.y) / 2f)
}