package com.example.fitnessapp.presentation.clients.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Client
import kotlinx.coroutines.launch

sealed class ClientDetailUiState {
    data object Loading : ClientDetailUiState()
    data class Success(val client: Client) : ClientDetailUiState()
    data class Error(val message: String) : ClientDetailUiState()
}

class ClientDetailViewModel : ViewModel() {

    private val repo = ServiceLocator.clientRepository

    var uiState by mutableStateOf<ClientDetailUiState>(ClientDetailUiState.Loading)
        private set
    var deleted by mutableStateOf(false)
        private set

    fun load(id: String) {
        viewModelScope.launch {
            uiState = ClientDetailUiState.Loading
            repo.getById(id).fold(
                onSuccess = { uiState = ClientDetailUiState.Success(it) },
                onFailure = { uiState = ClientDetailUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.delete(id).fold(
                onSuccess = { deleted = true },
                onFailure = { uiState = ClientDetailUiState.Error(it.message ?: "Не удалось удалить") }
            )
        }
    }
}
