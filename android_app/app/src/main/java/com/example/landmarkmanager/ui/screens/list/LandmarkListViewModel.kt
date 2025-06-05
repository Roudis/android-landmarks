package com.example.landmarkmanager.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.landmarkmanager.data.model.Landmark
import com.example.landmarkmanager.data.repository.LandmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandmarkListViewModel @Inject constructor(
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LandmarkListState>(LandmarkListState.Loading)
    val state: StateFlow<LandmarkListState> = _state

    private var searchJob: Job? = null

    init {
        loadLandmarks()
    }

    fun loadLandmarks(category: String? = null, title: String? = null) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = LandmarkListState.Loading
            // Add a small delay to prevent too many API calls while typing
            delay(300)
            repository.getLandmarks(category, title)
                .onSuccess { landmarks ->
                    _state.value = LandmarkListState.Success(landmarks)
                }
                .onFailure { error ->
                    _state.value = LandmarkListState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun deleteLandmark(id: Int) {
        viewModelScope.launch {
            _state.value = LandmarkListState.Loading
            repository.deleteLandmark(id)
                .onSuccess {
                    // Reload the landmarks list after successful deletion
                    loadLandmarks()
                }
                .onFailure { error ->
                    _state.value = LandmarkListState.Error(error.message ?: "Failed to delete landmark")
                }
        }
    }
}

sealed class LandmarkListState {
    object Loading : LandmarkListState()
    data class Success(val landmarks: List<Landmark>) : LandmarkListState()
    data class Error(val message: String) : LandmarkListState()
} 