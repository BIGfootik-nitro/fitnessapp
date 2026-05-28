package com.example.fitnessapp.presentation.sessions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.AuthEventBus
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.SessionAttendee
import com.example.fitnessapp.domain.model.TrainingSession
import kotlinx.coroutines.launch

sealed class SessionDetailUiState {
    data object Loading : SessionDetailUiState()
    data class Loaded(val session: TrainingSession, val attendees: List<SessionAttendee>) : SessionDetailUiState()
    data class Error(val message: String) : SessionDetailUiState()
}

class SessionDetailViewModel : ViewModel() {
    private val repo = ServiceLocator.sessionRepository
    var uiState by mutableStateOf<SessionDetailUiState>(SessionDetailUiState.Loading)
        private set

    fun load(sessionId: String) {
        viewModelScope.launch {
            uiState = SessionDetailUiState.Loading
            val sessionResult = repo.getById(sessionId)
            val session = sessionResult.getOrElse {
                if (it is UnauthorizedException) AuthEventBus.emitUnauthorized()
                else uiState = SessionDetailUiState.Error(it.message ?: "Ошибка")
                return@launch
            }
            val attendees = repo.getAttendees(sessionId).getOrElse { emptyList() }
            uiState = SessionDetailUiState.Loaded(session, attendees)
        }
    }

    fun markAttended(sessionId: String, bookingId: String) {
        viewModelScope.launch {
            repo.markAttended(sessionId, bookingId).onSuccess { load(sessionId) }
        }
    }

    fun delete(sessionId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.delete(sessionId).onSuccess { onDone() }
        }
    }
}
