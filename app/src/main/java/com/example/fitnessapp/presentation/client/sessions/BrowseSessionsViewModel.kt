package com.example.fitnessapp.presentation.client.sessions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.AuthEventBus
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.TrainingSession
import kotlinx.coroutines.launch

sealed class BrowseSessionsUiState {
    data object Loading : BrowseSessionsUiState()
    data class Loaded(val sessions: List<TrainingSession>, val successMessage: String? = null) : BrowseSessionsUiState()
    data class Error(val message: String) : BrowseSessionsUiState()
}

class BrowseSessionsViewModel : ViewModel() {
    private val repo = ServiceLocator.sessionRepository
    var uiState by mutableStateOf<BrowseSessionsUiState>(BrowseSessionsUiState.Loading)
        private set
    var bookingInProgress by mutableStateOf<String?>(null)
        private set

    fun load() {
        viewModelScope.launch {
            uiState = BrowseSessionsUiState.Loading
            repo.getAll().fold(
                onSuccess = { uiState = BrowseSessionsUiState.Loaded(it) },
                onFailure = { e ->
                    if (e is UnauthorizedException) AuthEventBus.emitUnauthorized()
                    else uiState = BrowseSessionsUiState.Error(e.message ?: "Ошибка")
                }
            )
        }
    }

    fun book(sessionId: String) {
        viewModelScope.launch {
            bookingInProgress = sessionId
            repo.book(sessionId).fold(
                onSuccess = {
                    load()
                    val current = uiState
                    if (current is BrowseSessionsUiState.Loaded)
                        uiState = current.copy(successMessage = "Вы успешно записаны!")
                },
                onFailure = { e ->
                    uiState = BrowseSessionsUiState.Error(e.message ?: "Ошибка записи")
                }
            )
            bookingInProgress = null
        }
    }

    fun clearSuccess() {
        val current = uiState
        if (current is BrowseSessionsUiState.Loaded) uiState = current.copy(successMessage = null)
    }
}
