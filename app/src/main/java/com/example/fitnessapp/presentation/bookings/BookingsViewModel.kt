package com.example.fitnessapp.presentation.bookings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Booking
import kotlinx.coroutines.launch

sealed class BookingsUiState {
    data object Loading : BookingsUiState()
    data class Loaded(val bookings: List<Booking>) : BookingsUiState()
    data class Error(val message: String) : BookingsUiState()
}

class BookingsViewModel : ViewModel() {

    private val bookingRepo = ServiceLocator.bookingRepository

    var uiState by mutableStateOf<BookingsUiState>(BookingsUiState.Loading)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            bookingRepo.getAll().fold(
                onSuccess = { uiState = BookingsUiState.Loaded(it) },
                onFailure = { uiState = BookingsUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun confirm(id: String) {
        viewModelScope.launch {
            bookingRepo.changeStatus(id, "CONFIRMED").onSuccess { load() }
        }
    }

    fun cancel(id: String) {
        viewModelScope.launch {
            bookingRepo.changeStatus(id, "CANCELLED").onSuccess { load() }
        }
    }
}
