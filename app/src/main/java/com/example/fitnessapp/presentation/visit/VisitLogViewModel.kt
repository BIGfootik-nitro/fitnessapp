package com.example.fitnessapp.presentation.visit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.di.ServiceLocator
import com.example.fitnessapp.domain.model.Visit
import kotlinx.coroutines.launch
import java.time.Instant

sealed class VisitUiState {
    data object Loading : VisitUiState()
    data class Success(val list: List<Visit>) : VisitUiState()
    data class Error(val message: String) : VisitUiState()
}

class VisitLogViewModel : ViewModel() {

    private val repo = ServiceLocator.visitRepository
    private var clientId: String = ""

    var uiState by mutableStateOf<VisitUiState>(VisitUiState.Loading)
        private set

    var showAddDialog by mutableStateOf(false)
    var note by mutableStateOf("")
    var error by mutableStateOf<String?>(null)

    fun load(id: String) {
        clientId = id
        viewModelScope.launch {
            uiState = VisitUiState.Loading
            repo.getByClient(id).fold(
                onSuccess = { uiState = VisitUiState.Success(it) },
                onFailure = { uiState = VisitUiState.Error(it.message ?: "Ошибка") }
            )
        }
    }

    fun openAdd() {
        note = ""
        error = null
        showAddDialog = true
    }

    fun add() {
        viewModelScope.launch {
            val now = Instant.now().toString()
            repo.add(clientId, now, note.takeIf { it.isNotBlank() }).fold(
                onSuccess = {
                    showAddDialog = false
                    load(clientId)
                },
                onFailure = { error = it.message ?: "Ошибка" }
            )
        }
    }
}
