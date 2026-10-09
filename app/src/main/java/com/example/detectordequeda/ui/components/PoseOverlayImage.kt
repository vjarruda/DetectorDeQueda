package com.example.detectordequeda.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import com.example.detectordequeda.domain.model.PoseAnalysis

//mostra a imagem com os pontos detectados e a linha do tronco desenhados por cima.
@Composable
fun PoseOverlayImage(bitmap: Bitmap, analysis: PoseAnalysis?) {
    val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()

    Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(ratio)) {
        drawImage(
            image = bitmap.asImageBitmap(),
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )

        analysis ?: return@Canvas

        analysis.landmarks.forEach { p ->
            drawCircle(Color.Red, radius = 6f, center = Offset(p.x * size.width, p.y * size.height))
        }

        drawLine(
            color = Color.Green,
            start = Offset(analysis.shoulderMid.x * size.width, analysis.shoulderMid.y * size.height),
            end = Offset(analysis.hipMid.x * size.width, analysis.hipMid.y * size.height),
            strokeWidth = 8f
        )
    }
}
