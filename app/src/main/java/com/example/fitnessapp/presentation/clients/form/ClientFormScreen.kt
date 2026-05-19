package com.example.fitnessapp.presentation.clients.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientFormScreen(
    clientId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ClientFormViewModel = viewModel()
) {
    LaunchedEffect(clientId) {
        if (clientId != null) viewModel.loadForEdit(clientId)
    }

    LaunchedEffect(viewModel.uiState) {
        if (viewModel.uiState is ClientFormUiState.Saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditMode) "Редактирование" else "Новый клиент") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = viewModel.fullName,
                onValueChange = { viewModel.fullName = it },
                label = { Text("ФИО *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.phone,
                onValueChange = { viewModel.phone = it },
                label = { Text("Телефон") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.birthDate,
                onValueChange = { viewModel.birthDate = it },
                label = { Text("Дата рождения (ГГГГ-ММ-ДД)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            val state = viewModel.uiState
            if (state is ClientFormUiState.Error) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.save() },
                enabled = state !is ClientFormUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state is ClientFormUiState.Loading) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Сохранить")
                }
            }
        }
    }
}
