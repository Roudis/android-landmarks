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

    fun loadLandmarks(title: String? = null) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                _state.value = LandmarkListState.Loading
                // Add a small delay for debouncing
                delay(300)
                
                // Only search if title is not empty
                val searchTitle = if (!title.isNullOrBlank()) title else null
                
                repository.getLandmarks(title = searchTitle)
                    .onSuccess { landmarks ->
                        _state.value = LandmarkListState.Success(landmarks)
                    }
                    .onFailure { error ->
                        _state.value = LandmarkListState.Error(error.message ?: "Failed to load landmarks")
                    }
            } catch (e: Exception) {
                _state.value = LandmarkListState.Error("An error occurred while loading landmarks")
            }
        }
    }

    fun deleteLandmark(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteLandmark(id)
                .onSuccess {
                    onSuccess()
                    loadLandmarks() // Reload the list after successful deletion
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