package com.example.fitnessapp.presentation.subscription

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
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType
import com.example.fitnessapp.ui.components.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    clientId: String,
    onBack: () -> Unit,
    onEditSub: (String) -> Unit = {},
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
            FloatingActionButton(onClick = { viewModel.openCreate() },
                containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, "Оформить")
            }
        }
    ) { padding ->
        val state = viewModel.uiState
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is SubscriptionUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is SubscriptionUiState.Error -> Text(state.message, Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error)
                is SubscriptionUiState.Success -> {
                    if (state.list.isEmpty()) {
                        Text("Абонементов нет", Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.list, key = { it.id }) { sub ->
                                TrainerSubscriptionCard(sub) { viewModel.toggleFreeze(sub.id) }
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
private fun TrainerSubscriptionCard(sub: Subscription, onToggleFreeze: () -> Unit) {
    val isActive = !sub.isFrozen && LocalDate.now().toString() <= sub.endDate
    val variant = when {
        sub.isFrozen -> BadgeVariant.FROZEN
        isActive -> BadgeVariant.ACTIVE
        else -> BadgeVariant.EXPIRED
    }
    val badgeText = when {
        sub.isFrozen -> "Заморожен"
        isActive -> "Активен"
        else -> "Истёк"
    }

    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(typeLabel(sub.type), fontSize = 18.sp, fontWeight = FontWeight.W600)
                StatusBadge(badgeText, variant)
            }
            Spacer(Modifier.height(4.dp))
            Text("${sub.startDate} — ${sub.endDate}", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${sub.price} руб.", fontSize = 14.sp, fontWeight = FontWeight.W500)
            Spacer(Modifier.height(8.dp))
            FitnessButton(if (sub.isFrozen) "Разморозить" else "Заморозить",
                onClick = onToggleFreeze, outlined = true)
        }
    }
}

@Composable
private fun CreateSubscriptionDialog(viewModel: SubscriptionViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showCreateDialog = false },
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text("Оформить абонемент") },
        text = {
            Column {
                Text("Тип", fontWeight = FontWeight.W500)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    SubscriptionType.values().forEach { t ->
                        FilterChip(selected = viewModel.type == t, onClick = { viewModel.type = t },
                            label = { Text(typeLabel(t)) })
                    }
                }
                Spacer(Modifier.height(8.dp))
                DatePickerField("Начало", viewModel.startDate, { viewModel.startDate = it },
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                DatePickerField("Конец", viewModel.endDate, { viewModel.endDate = it },
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = viewModel.price, onValueChange = { viewModel.price = it },
                    label = { Text("Цена") }, singleLine = true,
                    shape = MaterialTheme.shapes.medium)
                viewModel.formError?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = { FitnessButton("Создать", onClick = { viewModel.create() }) },
        dismissButton = { FitnessButton("Отмена", onClick = { viewModel.showCreateDialog = false }, outlined = true) }
    )
}

private fun typeLabel(t: SubscriptionType) = when (t) {
    SubscriptionType.MONTHLY -> "Месячный"
    SubscriptionType.QUARTERLY -> "Квартальный"
    SubscriptionType.ANNUAL -> "Годовой"
}
