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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update

@HiltViewModel
class LandmarkListViewModel @Inject constructor(
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LandmarkListState>(LandmarkListState.Loading)
    val state: StateFlow<LandmarkListState> = _state

    private var searchJob: Job? = null
    private var currentLandmarks: List<Landmark> = emptyList()

    init {
        loadLandmarks()
    }

    fun loadLandmarks(query: String? = null) {
        // Cancel any ongoing search
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            try {
                // Only show loading for initial load, not during search
                if (query.isNullOrEmpty() && _state.value !is LandmarkListState.Success) {
                    _state.value = LandmarkListState.Loading
                }

                // Add debounce delay only for search queries
                if (!query.isNullOrEmpty()) {
                    delay(300)
                }

                val result = repository.getLandmarks(search = query)
                result.onSuccess { landmarks ->
                    currentLandmarks = landmarks
                    _state.update { LandmarkListState.Success(landmarks) }
                }.onFailure { error ->
                    // If we have current landmarks and this is a search, keep showing them
                    if (query.isNullOrEmpty() || currentLandmarks.isEmpty()) {
                        _state.update { LandmarkListState.Error(error.message ?: "Failed to load landmarks") }
                    }
                }
            } catch (e: CancellationException) {
                // Ignore cancellation exceptions
                throw e
            } catch (e: Exception) {
                // If we have current landmarks and this is a search, keep showing them
                if (query.isNullOrEmpty() || currentLandmarks.isEmpty()) {
                    _state.update { LandmarkListState.Error(e.message ?: "An unexpected error occurred") }
                }
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

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}

sealed class LandmarkListState {
    object Loading : LandmarkListState()
    data class Success(val landmarks: List<Landmark>) : LandmarkListState()
    data class Error(val message: String) : LandmarkListState()
} 