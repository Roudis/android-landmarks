package com.example.landmarkmanager.data.model

import com.google.gson.annotations.SerializedName

data class Landmark(
    val id: Int = 0,
    val title: String,
    val category: String,
    val description: String,
    @SerializedName("cover_image")
    val imageUrl: String? = null,
    val latitude: Double,
    val longitude: Double
) 