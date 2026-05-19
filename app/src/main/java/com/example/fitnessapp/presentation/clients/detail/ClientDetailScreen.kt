package com.example.fitnessapp.presentation.clients.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onSubscriptions: (String) -> Unit,
    onVisits: (String) -> Unit,
    viewModel: ClientDetailViewModel = viewModel()
) {
    LaunchedEffect(clientId) { viewModel.load(clientId) }

    LaunchedEffect(viewModel.deleted) {
        if (viewModel.deleted) onBack()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Карточка клиента") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (state is ClientDetailUiState.Success) {
                        IconButton(onClick = { onEdit(clientId) }) {
                            Icon(Icons.Default.Edit, "Редактировать")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Удалить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is ClientDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ClientDetailUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                is ClientDetailUiState.Success -> {
                    val c = state.client
                    Column(Modifier.padding(16.dp)) {
                        Text(c.fullName, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(16.dp))
                        InfoRow("Телефон", c.phone ?: "—")
                        InfoRow("Email", c.email ?: "—")
                        InfoRow("Дата рождения", c.birthDate ?: "—")
                        Spacer(Modifier.height(24.dp))

                        OutlinedButton(
                            onClick = { onSubscriptions(clientId) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Абонементы") }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onVisits(clientId) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Посещения") }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить клиента?") },
            text = { Text("Это действие нельзя отменить") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete(clientId)
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
