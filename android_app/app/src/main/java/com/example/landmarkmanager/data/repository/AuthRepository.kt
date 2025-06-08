package com.example.landmarkmanager.data.repository

import com.example.landmarkmanager.data.remote.AuthService
import com.example.landmarkmanager.data.remote.LoginRequest
import com.example.landmarkmanager.data.remote.TokenResponse
import com.example.landmarkmanager.data.local.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<TokenResponse> {
        return try {
            val response = authService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val tokens = response.body()!!
                tokenManager.setTokens(tokens.access, tokens.refresh)
                Result.success(tokens)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<TokenResponse> {
        val refreshToken = tokenManager.getRefreshToken() ?: return Result.failure(Exception("No refresh token"))
        return try {
            val response = authService.refreshToken(mapOf("refresh" to refreshToken))
            if (response.isSuccessful && response.body() != null) {
                val tokens = response.body()!!
                tokenManager.setTokens(tokens.access, tokens.refresh)
                Result.success(tokens)
            } else {
                Result.failure(Exception("Token refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearTokens() {
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Boolean = tokenManager.getAccessToken() != null
} 