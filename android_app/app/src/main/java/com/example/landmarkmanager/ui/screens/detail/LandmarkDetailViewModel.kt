package com.example.landmarkmanager.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.landmarkmanager.data.model.Landmark
import com.example.landmarkmanager.data.repository.LandmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandmarkDetailViewModel @Inject constructor(
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LandmarkDetailState>(LandmarkDetailState.Loading)
    val state: StateFlow<LandmarkDetailState> = _state

    fun loadLandmark(id: Int) {
        viewModelScope.launch {
            _state.value = LandmarkDetailState.Loading
            repository.getLandmark(id)
                .onSuccess { landmark ->
                    _state.value = LandmarkDetailState.Success(landmark)
                }
                .onFailure { error ->
                    _state.value = LandmarkDetailState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun deleteLandmark(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = LandmarkDetailState.Loading
            repository.deleteLandmark(id)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    _state.value = LandmarkDetailState.Error(error.message ?: "Failed to delete landmark")
                }
        }
    }
}

sealed class LandmarkDetailState {
    object Loading : LandmarkDetailState()
    data class Success(val landmark: Landmark) : LandmarkDetailState()
    data class Error(val message: String) : LandmarkDetailState()
} 