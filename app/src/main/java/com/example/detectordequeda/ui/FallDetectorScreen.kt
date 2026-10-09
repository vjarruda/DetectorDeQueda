package com.example.detectordequeda.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detectordequeda.ui.components.PoseOverlayImage
import com.example.detectordequeda.ui.components.ResultSection

@Composable
fun FallDetectorScreen(
    uiState: FallDetectorUiState,
    onPhotoTaken: (Bitmap?) -> Unit,
    onImagePicked: (Uri?) -> Unit
) {
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview(), onPhotoTaken
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(), onImagePicked
    )

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DETECTOR DE QUEDA", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { cameraLauncher.launch(null) }) { Text("Tirar Foto") }
            Button(onClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) { Text("Galeria") }
        }

        Spacer(Modifier.height(16.dp))

        uiState.bitmap?.let { PoseOverlayImage(it, uiState.analysis) }

        Spacer(Modifier.height(16.dp))

        val analysis = uiState.analysis
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> Text(uiState.error, color = MaterialTheme.colorScheme.error)
            analysis != null -> ResultSection(analysis.result)
            uiState.noPersonDetected -> Text(
                "Nenhuma pessoa detectada.",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}