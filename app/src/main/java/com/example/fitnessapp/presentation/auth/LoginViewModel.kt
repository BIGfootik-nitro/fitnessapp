package com.example.fitnessapp.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val authRepository = ServiceLocator.authRepository

    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun onUsernameChange(value: String) { username = value }
    fun onPasswordChange(value: String) { password = value }

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            uiState = LoginUiState.Error("Введите логин и пароль")
            return
        }
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            authRepository.login(username.trim(), password).fold(
                onSuccess = { uiState = LoginUiState.Success },
                onFailure = { uiState = LoginUiState.Error(it.message ?: "Ошибка входа") }
            )
        }
    }

    fun resetError() {
        if (uiState is LoginUiState.Error) uiState = LoginUiState.Idle
    }
}
