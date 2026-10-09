package com.example.detectordequeda.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.detectordequeda.ui.FallDetectorScreen
import com.example.detectordequeda.ui.FallDetectorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FallDetectorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()
                    FallDetectorScreen(
                        uiState = uiState,
                        onPhotoTaken = viewModel::onPhotoTaken,
                        onImagePicked = viewModel::onImagePicked
                    )
                }
            }
        }
    }
}
