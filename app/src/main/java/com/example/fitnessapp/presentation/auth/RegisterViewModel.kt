package com.example.fitnessapp.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    data object Idle : RegisterUiState()
    data object Loading : RegisterUiState()
    data object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel : ViewModel() {

    private val authRepository = ServiceLocator.authRepository

    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordConfirm by mutableStateOf("")
        private set
    var uiState by mutableStateOf<RegisterUiState>(RegisterUiState.Idle)
        private set

    fun onUsernameChange(value: String) { username = value }
    fun onPasswordChange(value: String) { password = value }
    fun onPasswordConfirmChange(value: String) { passwordConfirm = value }

    var isClient by mutableStateOf(false)
        private set

    fun onRoleChange(client: Boolean) { isClient = client }

    fun register() {
        if (username.isBlank() || password.isBlank()) {
            uiState = RegisterUiState.Error("Заполните все поля")
            return
        }
        if (password.length < 6) {
            uiState = RegisterUiState.Error("Пароль не короче 6 символов")
            return
        }
        if (password != passwordConfirm) {
            uiState = RegisterUiState.Error("Пароли не совпадают")
            return
        }
        viewModelScope.launch {
            uiState = RegisterUiState.Loading
            val role = if (isClient) "CLIENT" else "TRAINER"
            authRepository.register(username.trim(), password, role).fold(
                onSuccess = { uiState = RegisterUiState.Success },
                onFailure = { uiState = RegisterUiState.Error(it.message ?: "Ошибка регистрации") }
            )
        }
    }

    fun resetError() {
        if (uiState is RegisterUiState.Error) uiState = RegisterUiState.Idle
    }
}
