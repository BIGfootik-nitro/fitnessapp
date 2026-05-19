package com.example.fitnessapp.presentation.clients.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Client
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class ClientListUiState {
    data object Loading : ClientListUiState()
    data class Success(val clients: List<Client>) : ClientListUiState()
    data class Error(val message: String) : ClientListUiState()
    data object Unauthorized : ClientListUiState()
}

class ClientListViewModel : ViewModel() {

    private val clientRepository = ServiceLocator.clientRepository

    var uiState by mutableStateOf<ClientListUiState>(ClientListUiState.Loading)
        private set
    var searchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    init {
        load()
    }

    fun onSearchChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // дебаунс
            load()
        }
    }

    fun load() {
        viewModelScope.launch {
            uiState = ClientListUiState.Loading
            clientRepository.getAll(searchQuery).fold(
                onSuccess = { uiState = ClientListUiState.Success(it) },
                onFailure = {
                    uiState = if (it is UnauthorizedException)
                        ClientListUiState.Unauthorized
                    else
                        ClientListUiState.Error(it.message ?: "Ошибка загрузки")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            ServiceLocator.authRepository.logout()
            uiState = ClientListUiState.Unauthorized
        }
    }
}
