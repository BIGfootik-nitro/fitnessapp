package com.example.fitnessapp.presentation.client.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.AuthEventBus
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Notification
import kotlinx.coroutines.launch

sealed class MyNotifsUiState {
    data object Loading : MyNotifsUiState()
    data class Loaded(val items: List<Notification>) : MyNotifsUiState()
    data class Error(val message: String) : MyNotifsUiState()
}

class MyNotificationsViewModel : ViewModel() {

    private val notifRepo = ServiceLocator.notificationRepository

    var uiState by mutableStateOf<MyNotifsUiState>(MyNotifsUiState.Loading)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            notifRepo.getMine().fold(
                onSuccess = { uiState = MyNotifsUiState.Loaded(it) },
                onFailure = { e ->
                    if (e is UnauthorizedException) AuthEventBus.emitUnauthorized()
                    else uiState = MyNotifsUiState.Error(e.message ?: "Ошибка")
                }
            )
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            notifRepo.markRead(id).onSuccess { load() }
        }
    }
}
