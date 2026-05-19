package com.example.fitnessapp.presentation.clients.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import kotlinx.coroutines.launch

sealed class ClientFormUiState {
    data object Idle : ClientFormUiState()
    data object Loading : ClientFormUiState()
    data object Saved : ClientFormUiState()
    data class Error(val message: String) : ClientFormUiState()
}

class ClientFormViewModel : ViewModel() {

    private val repo = ServiceLocator.clientRepository
    private var editId: String? = null

    var fullName by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var birthDate by mutableStateOf("")
    var uiState by mutableStateOf<ClientFormUiState>(ClientFormUiState.Idle)
        private set
    var isEditMode by mutableStateOf(false)
        private set

    fun loadForEdit(id: String) {
        editId = id
        isEditMode = true
        viewModelScope.launch {
            uiState = ClientFormUiState.Loading
            repo.getById(id).fold(
                onSuccess = { c ->
                    fullName = c.fullName
                    phone = c.phone.orEmpty()
                    email = c.email.orEmpty()
                    birthDate = c.birthDate.orEmpty()
                    uiState = ClientFormUiState.Idle
                },
                onFailure = { uiState = ClientFormUiState.Error(it.message ?: "Не удалось загрузить") }
            )
        }
    }

    fun save() {
        if (fullName.isBlank()) {
            uiState = ClientFormUiState.Error("Имя обязательно")
            return
        }
        viewModelScope.launch {
            uiState = ClientFormUiState.Loading
            val phoneVal = phone.takeIf { it.isNotBlank() }
            val emailVal = email.takeIf { it.isNotBlank() }
            val birthVal = birthDate.takeIf { it.isNotBlank() }
            val result = if (editId != null) {
                repo.update(editId!!, fullName.trim(), phoneVal, emailVal, birthVal)
            } else {
                repo.create(fullName.trim(), phoneVal, emailVal, birthVal).map { }
            }
            result.fold(
                onSuccess = { uiState = ClientFormUiState.Saved },
                onFailure = { uiState = ClientFormUiState.Error(it.message ?: "Не удалось сохранить") }
            )
        }
    }
}
