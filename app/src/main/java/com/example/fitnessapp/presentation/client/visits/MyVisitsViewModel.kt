package com.example.fitnessapp.presentation.client.visits

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.AuthEventBus
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Visit
import kotlinx.coroutines.launch

sealed class MyVisitsUiState {
    data object Loading : MyVisitsUiState()
    data class Loaded(val visits: List<Visit>) : MyVisitsUiState()
    data class Error(val message: String) : MyVisitsUiState()
}

class MyVisitsViewModel : ViewModel() {

    private val visitRepo = ServiceLocator.visitRepository

    var uiState by mutableStateOf<MyVisitsUiState>(MyVisitsUiState.Loading)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            visitRepo.getMine().fold(
                onSuccess = { uiState = MyVisitsUiState.Loaded(it) },
                onFailure = { e ->
                    if (e is UnauthorizedException) AuthEventBus.emitUnauthorized()
                    else uiState = MyVisitsUiState.Error(e.message ?: "Ошибка")
                }
            )
        }
    }
}
