package com.example.fitnessapp.presentation.client.subs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType
import com.example.fitnessapp.ui.components.*
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
            FloatingActionButton(onClick = { viewModel.openBuy() },
                containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, "Купить")
            }
        }
    ) { padding ->
        when (state) {
            is MySubsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is MySubsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is MySubsUiState.Loaded -> {
                if (state.subs.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center) {
                        Text("Нет абонементов. Нажмите + чтобы оформить", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                        .verticalScroll(rememberScrollState())) {
                        state.subs.forEach { sub ->
                            SubscriptionCard(sub)
                            Spacer(Modifier.height(12.dp))
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
    val isExpired = !isActive && !sub.isFrozen

    if (isActive) {
        // Active subscription — gradient card
        GradientCard(modifier = Modifier.fillMaxWidth()) {
            Text("АКТИВНЫЙ", fontSize = 12.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(4.dp))
            Text(typeLabel(sub.type), fontSize = 20.sp, fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.height(4.dp))
            Text("${sub.startDate} — ${sub.endDate}", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(2.dp))
            Text("${sub.price} руб.", fontSize = 14.sp, fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onPrimary)
        }
    } else {
        // Frozen or expired — outlined card with status badge
        OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
            Row(Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Column {
                    val variant = if (sub.isFrozen) BadgeVariant.FROZEN else BadgeVariant.EXPIRED
                    val labelText = if (sub.isFrozen) "ЗАМОРОЖЕН" else "ИСТЁК"
                    Text(labelText, fontSize = 12.sp, fontWeight = FontWeight.W500,
                        color = if (sub.isFrozen) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(4.dp))
                    Text(typeLabel(sub.type), fontSize = 20.sp, fontWeight = FontWeight.W600,
                        color = if (isExpired) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(4.dp))
                    Text("${sub.startDate} — ${sub.endDate}", fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(2.dp))
                    Text("${sub.price} руб.", fontSize = 14.sp, fontWeight = FontWeight.W500,
                        color = if (isExpired) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface)
                }
                StatusBadge(
                    text = if (sub.isFrozen) "Заморожен" else "Истёк",
                    variant = if (sub.isFrozen) BadgeVariant.FROZEN else BadgeVariant.EXPIRED
                )
            }
        }
    }
}

@Composable
private fun BuySubscriptionDialog(
    onBuy: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf("MONTHLY") }
    data class SubOption(val key: String, val label: String, val price: String)
    val options = listOf(
        SubOption("MONTHLY", "Месячный", "3 500 руб."),
        SubOption("QUARTERLY", "Квартальный", "9 000 руб."),
        SubOption("ANNUAL", "Годовой", "32 000 руб.")
    )
    val prices = mapOf("MONTHLY" to "3500.00", "QUARTERLY" to "9000.00", "ANNUAL" to "32000.00")
    val today = LocalDate.now()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text("Оформить абонемент", fontWeight = FontWeight.W500) },
        text = {
            Column {
                options.forEach { opt ->
                    val selected = selectedType == opt.key
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = selected, onClick = { selectedType = opt.key })
                        Column {
                            Text(opt.label, fontSize = 15.sp, fontWeight = FontWeight.W500)
                            Text(opt.price, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            FitnessButton("Оформить", onClick = {
                val start = today.toString()
                val end = when (selectedType) {
                    "MONTHLY" -> today.plusMonths(1).toString()
                    "QUARTERLY" -> today.plusMonths(3).toString()
                    else -> today.plusYears(1).toString()
                }
                onBuy(selectedType, start, end, prices[selectedType]!!)
            })
        },
        dismissButton = {
            FitnessButton("Отмена", onClick = onDismiss, outlined = true)
        }
    )
}

private fun typeLabel(type: SubscriptionType) = when (type) {
    SubscriptionType.MONTHLY -> "Месячный"
    SubscriptionType.QUARTERLY -> "Квартальный"
    SubscriptionType.ANNUAL -> "Годовой"
}
