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

sealed class LandmarkListState {
    object Loading : LandmarkListState()
    data class Success(val landmarks: List<Landmark>) : LandmarkListState()
    data class Error(val message: String) : LandmarkListState()
}

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

    fun loadLandmarks(search: String? = null) {
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            try {
                if (search.isNullOrEmpty() && _state.value !is LandmarkListState.Success) {
                    _state.value = LandmarkListState.Loading
                }

                if (!search.isNullOrEmpty()) {
                    delay(300)
                }

                val result = repository.getLandmarks(search = search)
                result.onSuccess { landmarks ->
                    currentLandmarks = landmarks
                    _state.value = LandmarkListState.Success(landmarks)
                }.onFailure { error ->
                    if (search.isNullOrEmpty() || currentLandmarks.isEmpty()) {
                        _state.value = LandmarkListState.Error(error.message ?: "Failed to load landmarks")
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (search.isNullOrEmpty() || currentLandmarks.isEmpty()) {
                    _state.value = LandmarkListState.Error(e.message ?: "An unexpected error occurred")
                }
            }
        }
    }

    fun updateSortedLandmarks(sortedLandmarks: List<Landmark>) {
        _state.value = LandmarkListState.Success(sortedLandmarks)
    }

    fun deleteLandmark(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteLandmark(id)
                .onSuccess {
                    onSuccess()
                    loadLandmarks()
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