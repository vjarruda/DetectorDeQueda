package com.example.detectordequeda.data.pose

import android.content.Context
import android.graphics.Bitmap
import com.example.detectordequeda.domain.model.Landmark
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker

class PoseDetector(context: Context) : AutoCloseable {

    private val poseLandmarker: PoseLandmarker

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(MODEL_PATH)
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.IMAGE)
            .setNumPoses(1)
            .build()

        poseLandmarker = PoseLandmarker.createFromOptions(context, options)
    }

    fun detect(bitmap: Bitmap): List<Landmark>? {
        val result = poseLandmarker.detect(BitmapImageBuilder(bitmap).build())
        return result.landmarks()
            .firstOrNull()
            ?.map { Landmark(it.x(), it.y()) }
    }

    override fun close() = poseLandmarker.close()

    private companion object {
        const val MODEL_PATH = "pose_landmarker_lite.task"
    }
}
