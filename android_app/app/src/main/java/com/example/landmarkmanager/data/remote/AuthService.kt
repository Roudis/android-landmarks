package com.example.landmarkmanager.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val refresh: String,
    val access: String
)

interface AuthService {
    @POST("api/token/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("api/token/refresh/")
    suspend fun refreshToken(@Body request: Map<String, String>): Response<TokenResponse>
} 