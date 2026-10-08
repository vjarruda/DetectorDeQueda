package com.example.detectordequeda

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseDetector(context: Context) {
    private var poseLandmarker: PoseLandmarker? = null

    init {
        // carrega modelo do pose landmark
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("pose_landmarker_lite.task")
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(com.google.mediapipe.tasks.vision.core.RunningMode.IMAGE)
            .setNumPoses(1)
            .build()

        poseLandmarker = PoseLandmarker.createFromOptions(context, options)
    }

    fun detect(bitmap: Bitmap): PoseLandmarkerResult? {
        val mpImage = BitmapImageBuilder(bitmap).build()
        return poseLandmarker?.detect(mpImage)
    }
}