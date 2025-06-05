package com.example.landmarkmanager.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.landmarkmanager.data.model.Landmark
import com.example.landmarkmanager.data.repository.LandmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddLandmarkViewModel @Inject constructor(
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AddLandmarkState>(AddLandmarkState.Initial)
    val state: StateFlow<AddLandmarkState> = _state

    fun addLandmark(
        title: String,
        description: String,
        category: String,
        imageFile: File?,
        latitude: Double?,
        longitude: Double?
    ) {
        viewModelScope.launch {
            _state.value = AddLandmarkState.Loading
            try {
                repository.addLandmark(
                    title = title,
                    description = description,
                    category = category,
                    imageFile = imageFile,
                    latitude = latitude,
                    longitude = longitude
                ).onSuccess { landmark ->
                    _state.value = AddLandmarkState.Success(landmark)
                }.onFailure { error ->
                    _state.value = AddLandmarkState.Error(error.message ?: "Failed to add landmark")
                }
            } catch (e: Exception) {
                _state.value = AddLandmarkState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}

sealed class AddLandmarkState {
    object Initial : AddLandmarkState()
    object Loading : AddLandmarkState()
    data class Success(val landmark: Landmark) : AddLandmarkState()
    data class Error(val message: String) : AddLandmarkState()
} 