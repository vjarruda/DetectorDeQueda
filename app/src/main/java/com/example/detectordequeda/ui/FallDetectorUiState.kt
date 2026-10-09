package com.example.detectordequeda.ui

import android.graphics.Bitmap
import com.example.detectordequeda.domain.model.PoseAnalysis

data class FallDetectorUiState(
    val bitmap: Bitmap? = null,
    val analysis: PoseAnalysis? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val noPersonDetected: Boolean
        get() = bitmap != null && analysis == null && !isLoading && error == null
}
