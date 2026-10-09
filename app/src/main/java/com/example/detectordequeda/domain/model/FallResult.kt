package com.example.detectordequeda.domain.model

enum class FallClassification(val label: String) {
    NORMAL("NORMAL"),
    ATTENTION("ATENÇÃO"),
    POSSIBLE_FALL("POSSÍVEL QUEDA")
}

data class FallResult(
    val angleDegrees: Float,
    val classification: FallClassification
)

//pontos detectados, linha do tronco e classificação
data class PoseAnalysis(
    val landmarks: List<Landmark>,
    val shoulderMid: Landmark,
    val hipMid: Landmark,
    val result: FallResult
)
