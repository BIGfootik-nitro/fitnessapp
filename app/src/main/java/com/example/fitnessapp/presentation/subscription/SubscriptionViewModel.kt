package com.example.fitnessapp.presentation.subscription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType
import kotlinx.coroutines.launch

sealed class SubscriptionUiState {
    data object Loading : SubscriptionUiState()
    data class Success(val list: List<Subscription>) : SubscriptionUiState()
    data class Error(val message: String) : SubscriptionUiState()
}

class SubscriptionViewModel : ViewModel() {

    private val repo = ServiceLocator.subscriptionRepository
    private var clientId: String = ""

    var uiState by mutableStateOf<SubscriptionUiState>(SubscriptionUiState.Loading)
        private set

    // поля формы создания
    var showCreateDialog by mutableStateOf(false)
    var type by mutableStateOf(SubscriptionType.MONTHLY)
    var startDate by mutableStateOf("")
    var endDate by mutableStateOf("")
    var price by mutableStateOf("")
    var formError by mutableStateOf<String?>(null)

    fun load(id: String) {
        clientId = id
        viewModelScope.launch {
            uiState = SubscriptionUiState.Loading
            repo.getByClient(id).fold(
                onSuccess = { uiState = SubscriptionUiState.Success(it) },
                onFailure = { uiState = SubscriptionUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun openCreate() {
        showCreateDialog = true
        type = SubscriptionType.MONTHLY
        startDate = ""
        endDate = ""
        price = ""
        formError = null
    }

    fun create() {
        if (startDate.isBlank() || endDate.isBlank() || price.isBlank()) {
            formError = "Заполните все поля"
            return
        }
        viewModelScope.launch {
            val normalizedPrice = price.trim().replace(",", ".").replace(" ", "")
            repo.create(clientId, type.name, startDate, endDate, normalizedPrice).fold(
                onSuccess = {
                    showCreateDialog = false
                    load(clientId)
                },
                onFailure = { formError = it.message ?: "Ошибка" }
            )
        }
    }

    fun toggleFreeze(subscriptionId: String) {
        viewModelScope.launch {
            repo.toggleFreeze(subscriptionId).fold(
                onSuccess = { load(clientId) },
                onFailure = { /* можно показать снэкбар */ }
            )
        }
    }
}
