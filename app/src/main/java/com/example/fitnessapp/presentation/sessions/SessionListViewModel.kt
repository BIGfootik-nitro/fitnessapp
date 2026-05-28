package com.example.fitnessapp.presentation.sessions

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

sealed class SessionListUiState {
    data object Loading : SessionListUiState()
    data class Loaded(val sessions: List<TrainingSession>) : SessionListUiState()
    data class Error(val message: String) : SessionListUiState()
}

class SessionListViewModel : ViewModel() {
    private val repo = ServiceLocator.sessionRepository
    var uiState by mutableStateOf<SessionListUiState>(SessionListUiState.Loading)
        private set

    fun load() {
        viewModelScope.launch {
            uiState = SessionListUiState.Loading
            repo.getAll().fold(
                onSuccess = { uiState = SessionListUiState.Loaded(it) },
                onFailure = { e ->
                    if (e is UnauthorizedException) AuthEventBus.emitUnauthorized()
                    else uiState = SessionListUiState.Error(e.message ?: "Ошибка")
                }
            )
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.delete(id).onSuccess { load() }
        }
    }
}
