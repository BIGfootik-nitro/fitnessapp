package com.example.fitnessapp.presentation.client.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Profile
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Loaded(val profile: Profile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data object Saved : ProfileUiState()
}

class ClientProfileViewModel : ViewModel() {

    private val profileRepo = ServiceLocator.profileRepository
    private val authRepo = ServiceLocator.authRepository

    var uiState by mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
        private set
    var fullName by mutableStateOf("")
        private set
    var phone by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var birthDate by mutableStateOf("")
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            profileRepo.getMe().fold(
                onSuccess = { p ->
                    fullName = p.client?.fullName ?: ""
                    phone = p.client?.phone ?: ""
                    email = p.client?.email ?: ""
                    birthDate = p.client?.birthDate ?: ""
                    uiState = ProfileUiState.Loaded(p)
                },
                onFailure = { uiState = ProfileUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun onFullNameChange(v: String) { fullName = v }
    fun onPhoneChange(v: String) { phone = v }
    fun onEmailChange(v: String) { email = v }
    fun onBirthDateChange(v: String) { birthDate = v }

    fun save() {
        viewModelScope.launch {
            profileRepo.updateMyProfile(
                fullName.trim(),
                phone.ifBlank { null },
                email.ifBlank { null },
                birthDate.ifBlank { null }
            ).fold(
                onSuccess = { uiState = ProfileUiState.Saved },
                onFailure = { uiState = ProfileUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun logout() {
        viewModelScope.launch { authRepo.logout() }
    }
}
