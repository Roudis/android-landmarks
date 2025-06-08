package com.example.landmarkmanager.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.landmarkmanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var loginState: LoginState by mutableStateOf(LoginState.Initial)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginState = LoginState.Loading
            try {
                val result = authRepository.login(email, password)
                result.fold(
                    onSuccess = {
                        loginState = LoginState.Success
                    },
                    onFailure = {
                        loginState = LoginState.Error(it.message ?: "Login failed")
                    }
                )
            } catch (e: Exception) {
                loginState = LoginState.Error(e.message ?: "An error occurred")
            }
        }
    }
} 