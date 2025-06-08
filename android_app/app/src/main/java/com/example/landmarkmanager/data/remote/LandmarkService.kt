package com.example.landmarkmanager.data.remote

import com.example.landmarkmanager.data.model.Landmark
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface LandmarkService {
    @GET("api/landmarks/")
    suspend fun getLandmarks(): Response<List<Landmark>>

    @GET("api/landmarks/{id}/")
    suspend fun getLandmark(@Path("id") id: Int): Response<Landmark>

    @POST("api/landmarks/")
    suspend fun createLandmark(@Body landmark: Landmark): Response<Landmark>

    @PUT("api/landmarks/{id}/")
    suspend fun updateLandmark(@Path("id") id: Int, @Body landmark: Landmark): Response<Landmark>

    @DELETE("api/landmarks/{id}/")
    suspend fun deleteLandmark(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("api/landmarks/{id}/upload_image/")
    suspend fun uploadImage(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Response<Landmark>
} 