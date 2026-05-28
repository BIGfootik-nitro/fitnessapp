package com.example.fitnessapp.presentation.client.subs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.AuthEventBus
import com.example.fitnessapp.data.repository.UnauthorizedException
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Subscription
import kotlinx.coroutines.launch

sealed class MySubsUiState {
    data object Loading : MySubsUiState()
    data class Loaded(val subs: List<Subscription>) : MySubsUiState()
    data class Error(val message: String) : MySubsUiState()
}

class MySubsViewModel : ViewModel() {

    private val subRepo = ServiceLocator.subscriptionRepository

    var uiState by mutableStateOf<MySubsUiState>(MySubsUiState.Loading)
        private set
    var showBuyDialog by mutableStateOf(false)
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = MySubsUiState.Loading
            subRepo.getMine().fold(
                onSuccess = { uiState = MySubsUiState.Loaded(it) },
                onFailure = { e ->
                    if (e is UnauthorizedException) AuthEventBus.emitUnauthorized()
                    else uiState = MySubsUiState.Error(e.message ?: "Ошибка")
                }
            )
        }
    }

    fun openBuy() { showBuyDialog = true }
    fun closeBuy() { showBuyDialog = false }

    fun buy(type: String, startDate: String, endDate: String, price: String) {
        viewModelScope.launch {
            val normalizedPrice = price.trim().replace(",", ".").replace(" ", "")
            subRepo.buyMine(type, startDate, endDate, normalizedPrice).fold(
                onSuccess = { closeBuy(); load() },
                onFailure = { uiState = MySubsUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }
}
