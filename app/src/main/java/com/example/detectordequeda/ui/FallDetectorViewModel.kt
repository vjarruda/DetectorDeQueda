package com.example.detectordequeda.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.detectordequeda.data.FallRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FallDetectorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FallRepository(application)

    private val _uiState = MutableStateFlow(FallDetectorUiState())
    val uiState: StateFlow<FallDetectorUiState> = _uiState.asStateFlow()

    fun onPhotoTaken(bitmap: Bitmap?) {
        bitmap ?: return
        process { repository.prepareBitmap(bitmap) }
    }

    fun onImagePicked(uri: Uri?) {
        uri ?: return
        process { repository.loadBitmap(uri) }
    }

    private fun process(loadBitmap: () -> Bitmap) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val bitmap = loadBitmap()
                val analysis = repository.analyze(bitmap)
                _uiState.update {
                    it.copy(bitmap = bitmap, analysis = analysis, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Erro ao processar a imagem.")
                }
            }
        }
    }

    override fun onCleared() {
        repository.close()
    }
}
