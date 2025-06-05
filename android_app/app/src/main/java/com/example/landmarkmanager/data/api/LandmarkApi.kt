package com.example.landmarkmanager.data.api

import com.example.landmarkmanager.data.model.Landmark
import okhttp3.MultipartBody
import retrofit2.http.*

interface LandmarkApi {
    @GET("landmarks/")
    suspend fun getLandmarks(
        @Query("category") category: String? = null,
        @Query("title") title: String? = null
    ): List<Landmark>

    @GET("landmarks/{id}/")
    suspend fun getLandmark(@Path("id") id: Int): Landmark

    @DELETE("landmarks/{id}/")
    suspend fun deleteLandmark(@Path("id") id: Int)

    @FormUrlEncoded
    @POST("landmarks/")
    suspend fun createLandmark(
        @Field("title") title: String,
        @Field("category") category: String,
        @Field("description") description: String,
        @Field("latitude") latitude: Double?,
        @Field("longitude") longitude: Double?
    ): Landmark

    @Multipart
    @POST("landmarks/")
    suspend fun createLandmarkWithImage(
        @Part("title") title: String,
        @Part("category") category: String,
        @Part("description") description: String,
        @Part("latitude") latitude: String,
        @Part("longitude") longitude: String,
        @Part cover_image: MultipartBody.Part
    ): Landmark
} 