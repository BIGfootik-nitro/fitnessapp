package com.example.fitnessapp.presentation.client.subs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySubsScreen(
    viewModel: MySubsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мои абонементы") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openBuy() }) {
                Icon(Icons.Default.Add, "Купить")
            }
        }
    ) { padding ->
        when (state) {
            is MySubsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
            is MySubsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is MySubsUiState.Loaded -> {
                if (state.subs.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding),
                        contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("Нет абонементов. Нажмите + чтобы оформить")
                    }
                } else {
                    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                        .verticalScroll(rememberScrollState())) {
                        state.subs.forEach { sub ->
                            SubscriptionCard(sub)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showBuyDialog) {
        BuySubscriptionDialog(
            onBuy = { type, start, end, price -> viewModel.buy(type, start, end, price) },
            onDismiss = { viewModel.closeBuy() }
        )
    }
}

@Composable
private fun SubscriptionCard(sub: Subscription) {
    val isActive = !sub.isFrozen && LocalDate.now().toString() <= sub.endDate
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(typeLabel(sub.type), fontWeight = FontWeight.SemiBold)
                if (sub.isFrozen) Text("Заморожен", color = MaterialTheme.colorScheme.tertiary)
                else if (isActive) Text("Активен", color = MaterialTheme.colorScheme.primary)
                else Text("Истёк", color = MaterialTheme.colorScheme.error)
            }
            Text("${sub.startDate} — ${sub.endDate}")
            Text("${sub.price} руб.")
        }
    }
}

@Composable
private fun BuySubscriptionDialog(
    onBuy: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf("MONTHLY") }
    val types = listOf("MONTHLY" to "Месячный — 3500 руб.", "QUARTERLY" to "Квартальный — 9000 руб.", "ANNUAL" to "Годовой — 32000 руб.")
    val prices = mapOf("MONTHLY" to "3500.00", "QUARTERLY" to "9000.00", "ANNUAL" to "32000.00")
    val today = LocalDate.now()

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Оформить абонемент") },
        text = {
            Column {
                types.forEach { (key, label) ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = selectedType == key, onClick = { selectedType = key })
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val start = today.toString()
                val end = when (selectedType) {
                    "MONTHLY" -> today.plusMonths(1).toString()
                    "QUARTERLY" -> today.plusMonths(3).toString()
                    else -> today.plusYears(1).toString()
                }
                onBuy(selectedType, start, end, prices[selectedType]!!)
            }) { Text("Оформить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

private fun typeLabel(type: SubscriptionType) = when (type) {
    SubscriptionType.MONTHLY -> "Месячный"
    SubscriptionType.QUARTERLY -> "Квартальный"
    SubscriptionType.ANNUAL -> "Годовой"
}
