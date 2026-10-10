package com.example.detectordequeda.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.detectordequeda.data.image.ImageLoader
import com.example.detectordequeda.data.pose.PoseDetector
import com.example.detectordequeda.domain.FallDetector
import com.example.detectordequeda.domain.model.PoseAnalysis

class FallRepository(context: Context) {

    private val imageLoader = ImageLoader(context)
    private val poseDetector = PoseDetector(context)

    fun loadBitmap(uri: Uri): Bitmap = imageLoader.loadFromUri(uri)

    fun prepareBitmap(bitmap: Bitmap): Bitmap = imageLoader.toArgb(bitmap)

    //detecta a pose e classifica a postura. Retorna null se não houver pessoa.
    fun analyze(bitmap: Bitmap): PoseAnalysis? {
        val landmarks = poseDetector.detect(bitmap) ?: return null
        val aspectRatio = bitmap.width.toFloat() / bitmap.height
        return FallDetector.analyze(landmarks, aspectRatio)
    }

    fun close() = poseDetector.close()
}
