package com.example.landmarkmanager.ui.screens.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.landmarkmanager.data.repository.LandmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AddLandmarkViewModel @Inject constructor(
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AddLandmarkState>(AddLandmarkState.Initial)
    val state: StateFlow<AddLandmarkState> = _state

    fun addLandmark(
        title: String,
        category: String = "OTHER",
        description: String = "",
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        imageUri: Uri? = null,
        context: Context
    ) {
        viewModelScope.launch {
            _state.value = AddLandmarkState.Loading
            try {
                val image = imageUri?.let { uri ->
                    // Create a temporary file to store the image
                    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Create MultipartBody.Part from the file
                    val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
                }

                repository.createLandmark(
                    title = title,
                    category = category,
                    description = description.ifEmpty { "No description provided" },
                    latitude = latitude,
                    longitude = longitude,
                    image = image
                ).onSuccess {
                    _state.value = AddLandmarkState.Success
                }.onFailure { error ->
                    _state.value = AddLandmarkState.Error(error.message ?: "Failed to save landmark")
                }
            } catch (e: Exception) {
                _state.value = AddLandmarkState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class AddLandmarkState {
    object Initial : AddLandmarkState()
    object Loading : AddLandmarkState()
    object Success : AddLandmarkState()
    data class Error(val message: String) : AddLandmarkState()
} 