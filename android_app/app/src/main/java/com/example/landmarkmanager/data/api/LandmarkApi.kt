package com.example.landmarkmanager.data.api

import com.example.landmarkmanager.data.model.Landmark
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface LandmarkApi {
    @GET("landmarks/")
    suspend fun getLandmarks(
        @Query("search") search: String? = null
    ): List<Landmark>

    @GET("landmarks/{id}/")
    suspend fun getLandmark(@Path("id") id: Int): Landmark

    @Multipart
    @POST("landmarks/")
    suspend fun addLandmark(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("country") country: RequestBody?
    ): Landmark

    @DELETE("landmarks/{id}/")
    suspend fun deleteLandmark(@Path("id") id: Int)

    @Multipart
    @PUT("landmarks/{id}/")
    suspend fun updateLandmark(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("country") country: RequestBody?
    ): Landmark
} 