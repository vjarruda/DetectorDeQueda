package com.example.detectordequeda

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.atan

data class FallResult(
    val angleDegrees: Float,
    val classification: String
)

object FallDetector {
    //ângulo para determinar a postura
    private const val THRESHOLD_ATTENTION = 45f
    private const val THRESHOLD_FALL = 65f

    fun analyzePose(landmarks: List<NormalizedLandmark>): FallResult {
        //ponto dos ombros
        val leftShoulder = landmarks[11]
        val rightShoulder = landmarks[12]
        // ponto dos quadris
        val leftHip = landmarks[23]
        val rightHip = landmarks[24]

        // calculo do centro dos ombros e centro dos quadris
        val midShoulderX = (leftShoulder.x() + rightShoulder.x()) / 2f
        val midShoulderY = (leftShoulder.y() + rightShoulder.y()) / 2f
        val midHipX = (leftHip.x() + rightHip.x()) / 2f
        val midHipY = (leftHip.y() + rightHip.y()) / 2f

        //diferença entre eles
        val dx = midHipX - midShoulderX
        val dy = midHipY - midShoulderY

        //calcula angulo em relacao a linha vertical
        val angleRad = if (dy == 0f) Math.PI / 2.0 else atan(abs(dx.toDouble()) / abs(dy.toDouble()))
        val angleDeg = Math.toDegrees(angleRad).toFloat()

        //verificar inclinacao do tronco
        val classification = when {
            angleDeg > THRESHOLD_FALL -> "POSSÍVEL QUEDA"
            angleDeg > THRESHOLD_ATTENTION -> "ATENÇÃO"
            else -> "NORMAL"
        }

        return FallResult(angleDeg, classification)
    }
}