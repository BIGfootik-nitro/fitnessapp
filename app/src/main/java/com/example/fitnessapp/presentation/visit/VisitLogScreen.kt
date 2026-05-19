package com.example.fitnessapp.presentation.visit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.domain.model.Visit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitLogScreen(
    clientId: String,
    onBack: () -> Unit,
    viewModel: VisitLogViewModel = viewModel()
) {
    LaunchedEffect(clientId) { viewModel.load(clientId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Посещения") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openAdd() }) {
                Icon(Icons.Default.Add, "Отметить")
            }
        }
    ) { padding ->
        val state = viewModel.uiState
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is VisitUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is VisitUiState.Error -> Text(state.message, Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                is VisitUiState.Success -> {
                    if (state.list.isEmpty()) {
                        Text("Посещений нет", Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(state.list, key = { it.id }) { v ->
                                VisitItem(v)
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddDialog = false },
            title = { Text("Зафиксировать посещение") },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.note,
                        onValueChange = { viewModel.note = it },
                        label = { Text("Комментарий (необязательно)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    viewModel.error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.add() }) { Text("Отметить") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun VisitItem(visit: Visit) {
    ListItem(
        headlineContent = { Text(visit.visitedAt.replace("T", " ").substringBefore(".")) },
        supportingContent = visit.note?.let { { Text(it) } }
    )
}
