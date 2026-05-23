package com.example.fitnessapp.presentation.client.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.Profile
import com.example.fitnessapp.domain.model.Subscription
import com.example.fitnessapp.domain.model.SubscriptionType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    onSubscriptionsClick: () -> Unit,
    onBookClick: () -> Unit,
    onVisitsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: ClientHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Фитнес-центр") })
        }
    ) { padding ->
        when (state) {
            is ClientHomeUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is ClientHomeUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is ClientHomeUiState.Loaded -> {
                val name = state.profile.client?.fullName ?: state.profile.username
                Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                    .verticalScroll(rememberScrollState())) {
                    Text("Привет, $name!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(20.dp))

                    if (state.activeSubs.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Активный абонемент", fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.height(8.dp))
                                val sub = state.activeSubs.first()
                                Text(typeLabel(sub.type), fontSize = 18.sp, fontWeight = FontWeight.Medium)
                                Text("до ${sub.endDate}")
                                if (LocalDate.now().toString() > sub.endDate) {
                                    Text("Истёк", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    } else {
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.height(8.dp))
                                Text("Нет активного абонемента")
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = onSubscriptionsClick) { Text("Оформить") }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("Действия", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))

                    ActionCard(Icons.Default.EventAvailable, "Записаться на тренировку", onBookClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.CardMembership, "Мои абонементы", onSubscriptionsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.History, "История посещений", onVisitsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.Notifications, "Уведомления", onNotificationsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.Person, "Профиль", onProfileClick)
                }
            }
        }
    }
}

@Composable
private fun ActionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 16.sp)
        }
    }
}

private fun typeLabel(type: SubscriptionType) = when (type) {
    SubscriptionType.MONTHLY -> "Месячный"
    SubscriptionType.QUARTERLY -> "Квартальный"
    SubscriptionType.ANNUAL -> "Годовой"
}
