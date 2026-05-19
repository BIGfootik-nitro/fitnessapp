package com.example.fitnessapp.presentation.subscription

import androidx.compose.foundation.clickable
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
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    clientId: String,
    onBack: () -> Unit,
    viewModel: SubscriptionViewModel = viewModel()
) {
    LaunchedEffect(clientId) { viewModel.load(clientId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Абонементы") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openCreate() }) {
                Icon(Icons.Default.Add, "Оформить")
            }
        }
    ) { padding ->
        val state = viewModel.uiState
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is SubscriptionUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is SubscriptionUiState.Error -> Text(state.message, Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                is SubscriptionUiState.Success -> {
                    if (state.list.isEmpty()) {
                        Text("Абонементов нет", Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(state.list, key = { it.id }) { sub ->
                                SubscriptionItem(sub) { viewModel.toggleFreeze(sub.id) }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showCreateDialog) {
        CreateSubscriptionDialog(viewModel)
    }
}

@Composable
private fun SubscriptionItem(sub: Subscription, onToggleFreeze: () -> Unit) {
    ListItem(
        headlineContent = { Text(typeLabel(sub.type)) },
        supportingContent = {
            Column {
                Text("${sub.startDate} — ${sub.endDate}")
                Text("Цена: ${sub.price}")
                if (sub.isFrozen) Text("Заморожен", color = MaterialTheme.colorScheme.primary)
            }
        },
        trailingContent = {
            TextButton(onClick = onToggleFreeze) {
                Text(if (sub.isFrozen) "Разморозить" else "Заморозить")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSubscriptionDialog(viewModel: SubscriptionViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showCreateDialog = false },
        title = { Text("Оформить абонемент") },
        text = {
            Column {
                Text("Тип")
                Row {
                    SubscriptionType.values().forEach { t ->
                        FilterChip(
                            selected = viewModel.type == t,
                            onClick = { viewModel.type = t },
                            label = { Text(typeLabel(t)) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.startDate,
                    onValueChange = { viewModel.startDate = it },
                    label = { Text("Начало (ГГГГ-ММ-ДД)") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.endDate,
                    onValueChange = { viewModel.endDate = it },
                    label = { Text("Конец (ГГГГ-ММ-ДД)") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.price,
                    onValueChange = { viewModel.price = it },
                    label = { Text("Цена") },
                    singleLine = true
                )
                viewModel.formError?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { viewModel.create() }) { Text("Создать") }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showCreateDialog = false }) { Text("Отмена") }
        }
    )
}

private fun typeLabel(t: SubscriptionType) = when (t) {
    SubscriptionType.MONTHLY -> "Месячный"
    SubscriptionType.QUARTERLY -> "Квартальный"
    SubscriptionType.ANNUAL -> "Годовой"
}
