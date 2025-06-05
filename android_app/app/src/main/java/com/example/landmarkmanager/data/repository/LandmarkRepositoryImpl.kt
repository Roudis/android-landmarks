package com.example.landmarkmanager.data.repository

import com.example.landmarkmanager.data.api.LandmarkApi
import com.example.landmarkmanager.data.model.Landmark
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandmarkRepositoryImpl @Inject constructor(
    private val api: LandmarkApi
) : LandmarkRepository {
    override suspend fun getLandmarks(search: String?): Result<List<Landmark>> {
        return try {
            Result.success(api.getLandmarks(search))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLandmark(id: Int): Result<Landmark> {
        return try {
            Result.success(api.getLandmark(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addLandmark(
        title: String,
        description: String,
        category: String,
        imageFile: File?,
        latitude: Double?,
        longitude: Double?
    ): Result<Landmark> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudeBody = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                MultipartBody.Part.createFormData(
                    "cover_image",
                    it.name,
                    it.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }

            Result.success(
                api.addLandmark(
                    title = titleBody,
                    description = descriptionBody,
                    category = categoryBody,
                    image = imagePart,
                    latitude = latitudeBody,
                    longitude = longitudeBody
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLandmark(id: Int): Result<Unit> {
        return try {
            api.deleteLandmark(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLandmark(
        id: Int,
        title: String,
        description: String,
        category: String,
        imageFile: File?,
        latitude: Double?,
        longitude: Double?
    ): Result<Landmark> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudeBody = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                MultipartBody.Part.createFormData(
                    "cover_image",
                    it.name,
                    it.asRequestBody("image/*".toMediaTypeOrNull())
                )
            }

            Result.success(
                api.updateLandmark(
                    id = id,
                    title = titleBody,
                    description = descriptionBody,
                    category = categoryBody,
                    image = imagePart,
                    latitude = latitudeBody,
                    longitude = longitudeBody
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 