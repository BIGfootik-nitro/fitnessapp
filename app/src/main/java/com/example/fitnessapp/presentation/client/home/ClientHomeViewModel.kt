package com.example.fitnessapp.presentation.client.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Profile
import com.example.fitnessapp.domain.model.Subscription
import kotlinx.coroutines.launch

sealed class ClientHomeUiState {
    data object Loading : ClientHomeUiState()
    data class Loaded(val profile: Profile, val activeSubs: List<Subscription>) : ClientHomeUiState()
    data class Error(val message: String) : ClientHomeUiState()
}

class ClientHomeViewModel : ViewModel() {

    private val profileRepo = ServiceLocator.profileRepository
    private val subRepo = ServiceLocator.subscriptionRepository

    var uiState by mutableStateOf<ClientHomeUiState>(ClientHomeUiState.Loading)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = ClientHomeUiState.Loading
            val profileResult = profileRepo.getMe()
            val profile = profileResult.getOrElse {
                uiState = ClientHomeUiState.Error(it.message ?: "Ошибка"); return@launch
            }
            val subsResult = subRepo.getMine()
            val subs = subsResult.getOrElse { emptyList() }
            uiState = ClientHomeUiState.Loaded(profile, subs.filter {
                it.endDate >= java.time.LocalDate.now().toString() && !it.isFrozen
            })
        }
    }
}
