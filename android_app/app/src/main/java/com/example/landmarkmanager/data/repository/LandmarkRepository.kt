package com.example.landmarkmanager.data.repository

import com.example.landmarkmanager.data.model.Landmark
import java.io.File

interface LandmarkRepository {
    suspend fun getLandmarks(search: String? = null): Result<List<Landmark>>
    suspend fun getLandmark(id: Int): Result<Landmark>
    suspend fun addLandmark(
        title: String,
        description: String,
        category: String,
        imageFile: File?,
        latitude: Double?,
        longitude: Double?,
        country: String?
    ): Result<Landmark>
    suspend fun deleteLandmark(id: Int): Result<Unit>
    suspend fun updateLandmark(
        id: Int,
        title: String,
        description: String,
        category: String,
        imageFile: File?,
        latitude: Double?,
        longitude: Double?,
        country: String?
    ): Result<Landmark>
} 