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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.domain.model.Visit
import com.example.fitnessapp.ui.components.FitnessButton
import com.example.fitnessapp.ui.theme.Primary
import com.example.fitnessapp.ui.theme.PrimaryContainer

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
            FloatingActionButton(onClick = { viewModel.openAdd() },
                containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, "Отметить")
            }
        }
    ) { padding ->
        val state = viewModel.uiState
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is VisitUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is VisitUiState.Error -> Text(state.message, Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error)
                is VisitUiState.Success -> {
                    if (state.list.isEmpty()) {
                        Text("Посещений нет", Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.list, key = { it.id }) { v -> VisitCard(v) }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddDialog = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text("Зафиксировать посещение") },
            text = {
                Column {
                    OutlinedTextField(value = viewModel.note, onValueChange = { viewModel.note = it },
                        label = { Text("Комментарий (необязательно)") },
                        modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    viewModel.error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = { FitnessButton("Отметить", onClick = { viewModel.add() }) },
            dismissButton = { FitnessButton("Отмена", onClick = { viewModel.showAddDialog = false }, outlined = true) }
        )
    }
}

@Composable
private fun VisitCard(visit: Visit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Row(Modifier.padding(20.dp)) {
            Surface(shape = MaterialTheme.shapes.medium, color = PrimaryContainer,
                modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    val day = visit.visitedAt.substring(8, 10)
                    Text(day, fontSize = 14.sp, fontWeight = FontWeight.W600, color = Primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(visit.visitedAt.replace("T", " ").substringBefore("."),
                    fontSize = 16.sp, fontWeight = FontWeight.W600)
                visit.note?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
