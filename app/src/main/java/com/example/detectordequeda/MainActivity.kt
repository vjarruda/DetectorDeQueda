package com.example.detectordequeda

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    FallDetectorApp()
                }
            }
        }
    }
}

@Composable
fun FallDetectorApp() {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var result by remember { mutableStateOf<FallResult?>(null) }
    var landmarks by remember { mutableStateOf<List<NormalizedLandmark>?>(null) }

    val poseDetector = remember { PoseDetector(context) }

    // camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) {
            val argbBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true)
            bitmap = argbBitmap
            val detection = poseDetector.detect(argbBitmap)

            if (detection != null && detection.landmarks().isNotEmpty()) {
                landmarks = detection.landmarks()[0]
                result = FallDetector.analyzePose(landmarks!!)
            } else {
                landmarks = null
                result = null
            }
        }
    }

    // funcionalidade de pegar foto da galeria
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val rawBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val argbBitmap = rawBitmap.copy(Bitmap.Config.ARGB_8888, true)
            bitmap = argbBitmap
            val detection = poseDetector.detect(argbBitmap)

            if (detection != null && detection.landmarks().isNotEmpty()) {
                landmarks = detection.landmarks()[0]
                result = FallDetector.analyzePose(landmarks!!)
            } else {
                landmarks = null
                result = null
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DETECTOR DE QUEDA", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { cameraLauncher.launch(null) }) {
                Text("Tirar Foto")
            }
            Button(onClick = {
                galleryLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text("Galeria")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let { bmp ->
            val ratio = bmp.width.toFloat() / bmp.height.toFloat()
            Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(ratio)) {
                drawImage(image = bmp.asImageBitmap())

                landmarks?.let { points ->
                    points.forEach { p ->
                        drawCircle(Color.Red, radius = 6f, center = Offset(p.x() * size.width, p.y() * size.height))
                    }

                    val lShoulder = points[11]
                    val rShoulder = points[12]
                    val lHip = points[23]
                    val rHip = points[24]

                    val midShoulderX = ((lShoulder.x() + rShoulder.x()) / 2f) * size.width
                    val midShoulderY = ((lShoulder.y() + rShoulder.y()) / 2f) * size.height
                    val midHipX = ((lHip.x() + rHip.x()) / 2f) * size.width
                    val midHipY = ((lHip.y() + rHip.y()) / 2f) * size.height

                    drawLine(
                        color = Color.Green,
                        start = Offset(midShoulderX, midShoulderY),
                        end = Offset(midHipX, midHipY),
                        strokeWidth = 8f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        result?.let { r ->
            Text("Ângulo do tronco: ${"%.1f".format(r.angleDegrees)}°", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            val color = when (r.classification) {
                "POSSÍVEL QUEDA" -> Color.Red
                "ATENÇÃO" -> Color(0xFFFFA500)
                else -> Color(0xFF008000) // Verde Escuro
            }
            Text(
                text = "Resultado:\n${r.classification}",
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } ?: run {
            if (bitmap != null) {
                Text("Nenhuma pessoa detectada.", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}