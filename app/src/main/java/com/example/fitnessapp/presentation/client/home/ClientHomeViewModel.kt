package com.example.fitnessapp.presentation.client.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.AuthEventBus
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Profile
import com.example.fitnessapp.domain.model.Subscription
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class ClientHomeUiState {
    data object Loading : ClientHomeUiState()
    data class Loaded(val profile: Profile, val activeSubs: List<Subscription>, val unreadNotifs: Int = 0) : ClientHomeUiState()
    data class Error(val message: String) : ClientHomeUiState()
}

class ClientHomeViewModel : ViewModel() {

    private val profileRepo = ServiceLocator.profileRepository
    private val subRepo = ServiceLocator.subscriptionRepository
    private val notifRepo = ServiceLocator.notificationRepository

    var uiState by mutableStateOf<ClientHomeUiState>(ClientHomeUiState.Loading)
        private set

    fun load() {
        viewModelScope.launch {
            uiState = ClientHomeUiState.Loading
            val profileResult = profileRepo.getMe()
            val profile = profileResult.getOrElse {
                if (it is UnauthorizedException) AuthEventBus.emitUnauthorized()
                else uiState = ClientHomeUiState.Error(it.message ?: "Ошибка")
                return@launch
            }
            val subsResult = subRepo.getMine()
            val subs = subsResult.getOrElse { emptyList() }
            val notifsResult = notifRepo.getMine()
            val unread = notifsResult.getOrElse { emptyList() }.count { !it.read }
            val today = LocalDate.now().toString()
            uiState = ClientHomeUiState.Loaded(
                profile,
                subs.filter { it.endDate >= today && !it.isFrozen },
                unread
            )
        }
    }
}
