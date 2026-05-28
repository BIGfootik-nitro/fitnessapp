package com.example.fitnessapp.presentation.subscription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import kotlinx.coroutines.launch

class EditSubscriptionViewModel : ViewModel() {
    private val subRepo = ServiceLocator.subscriptionRepository

    var loading by mutableStateOf(false)
    var type by mutableStateOf("MONTHLY")
    var startDate by mutableStateOf("")
    var endDate by mutableStateOf("")
    var price by mutableStateOf("")
    var error by mutableStateOf<String?>(null)

    fun load(id: String) {
        // pre-populate when called from SubscriptionScreen with known data
    }

    fun prefill(type: String, startDate: String, endDate: String, price: String) {
        this.type = type
        this.startDate = startDate
        this.endDate = endDate
        this.price = price
    }

    fun save(id: String, onDone: () -> Unit) {
        viewModelScope.launch {
            error = null
            subRepo.updateSub(id, type, startDate, endDate, price).fold(
                onSuccess = { onDone() },
                onFailure = { error = it.message }
            )
        }
    }

    fun delete(id: String, onDone: () -> Unit) {
        viewModelScope.launch {
            subRepo.deleteSub(id).fold(
                onSuccess = { onDone() },
                onFailure = { error = it.message }
            )
        }
    }
}
