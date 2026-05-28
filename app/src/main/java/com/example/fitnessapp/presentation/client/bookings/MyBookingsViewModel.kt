package com.example.fitnessapp.presentation.client.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.AuthEventBus
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Booking
import kotlinx.coroutines.launch

sealed class MyBookingsUiState {
    data object Loading : MyBookingsUiState()
    data class Loaded(val bookings: List<Booking>) : MyBookingsUiState()
    data class Error(val message: String) : MyBookingsUiState()
}

class MyBookingsViewModel : ViewModel() {

    private val bookingRepo = ServiceLocator.bookingRepository

    var uiState by mutableStateOf<MyBookingsUiState>(MyBookingsUiState.Loading)
        private set
    var showCreateDialog by mutableStateOf(false)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = MyBookingsUiState.Loading
            bookingRepo.getMine().fold(
                onSuccess = { uiState = MyBookingsUiState.Loaded(it) },
                onFailure = { e ->
                    if (e is UnauthorizedException) AuthEventBus.emitUnauthorized()
                    else uiState = MyBookingsUiState.Error(e.message ?: "Ошибка")
                }
            )
        }
    }

    fun openCreate() { showCreateDialog = true }
    fun closeCreate() { showCreateDialog = false }

    fun create(scheduledAtIso: String, note: String?) {
        viewModelScope.launch {
            bookingRepo.create(scheduledAtIso, note).fold(
                onSuccess = { closeCreate(); load() },
                onFailure = { uiState = MyBookingsUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun cancel(id: String) {
        viewModelScope.launch {
            bookingRepo.cancelMine(id).fold(
                onSuccess = { load() },
                onFailure = { uiState = MyBookingsUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }
}
