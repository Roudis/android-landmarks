package com.example.landmarkmanager.data.repository

import com.example.landmarkmanager.data.api.LandmarkApi
import com.example.landmarkmanager.data.model.Landmark
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandmarkRepository @Inject constructor(
    private val api: LandmarkApi
) {
    suspend fun getLandmarks(category: String? = null, title: String? = null): Result<List<Landmark>> {
        return try {
            Result.success(api.getLandmarks(category, title))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLandmark(id: Int): Result<Landmark> {
        return try {
            Result.success(api.getLandmark(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLandmark(id: Int): Result<Unit> {
        return try {
            api.deleteLandmark(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createLandmark(
        title: String,
        category: String,
        description: String,
        latitude: Double?,
        longitude: Double?,
        image: MultipartBody.Part?
    ): Result<Landmark> {
        return try {
            val response = if (image != null) {
                api.createLandmarkWithImage(
                    title = title,
                    category = category,
                    description = description,
                    latitude = latitude?.toString() ?: "",
                    longitude = longitude?.toString() ?: "",
                    cover_image = image
                )
            } else {
                api.createLandmark(
                    title = title,
                    category = category,
                    description = description,
                    latitude = latitude,
                    longitude = longitude
                )
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 