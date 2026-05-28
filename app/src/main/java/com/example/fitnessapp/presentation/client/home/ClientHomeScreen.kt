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
import com.example.fitnessapp.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    onSubscriptionsClick: () -> Unit,
    onSessionsClick: () -> Unit,
    onBookClick: () -> Unit,
    onVisitsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: ClientHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    // refresh when returning to this screen
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фитнес-центр") },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(Icons.Default.Notifications, null)
                    }
                }
            )
        }
    ) { padding ->
        when (state) {
            is ClientHomeUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is ClientHomeUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        FitnessButton("Повторить", onClick = { viewModel.load() })
                    }
                }
            is ClientHomeUiState.Loaded -> {
                val name = state.profile.client?.fullName ?: state.profile.username
                Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                    .verticalScroll(rememberScrollState())) {

                    Text("Привет, $name!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(20.dp))

                    // Active subscription card with gradient
                    if (state.activeSubs.isNotEmpty()) {
                        val sub = state.activeSubs.first()
                        GradientCard(modifier = Modifier.fillMaxWidth()) {
                            Text("АКТИВНЫЙ АБОНИМЕНТ", fontSize = 14.sp, fontWeight = FontWeight.W500,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.height(4.dp))
                            Text(typeLabel(sub.type), fontSize = 22.sp, fontWeight = FontWeight.W600,
                                color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.height(4.dp))
                            Text("Действует до ${sub.endDate}", fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    } else {
                        OutlinedCard(modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large) {
                            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Нет активного абонемента", fontWeight = FontWeight.W500)
                                Spacer(Modifier.height(12.dp))
                                FitnessButton("Оформить", onClick = onSubscriptionsClick)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("Действия", fontSize = 18.sp, fontWeight = FontWeight.W600)
                    Spacer(Modifier.height(12.dp))

                    ActionCard(Icons.Default.FitnessCenter, "Тренировки", "Расписание и запись на занятия",
                        onClick = onSessionsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.EventAvailable, "Мои записи", "Ваши предстоящие тренировки",
                        badgeCount = state.unreadNotifs, onClick = onBookClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.CardMembership, "Мои абонементы", "Просмотр и оформление",
                        onClick = onSubscriptionsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.History, "История посещений", "Ваши прошлые тренировки",
                        onClick = onVisitsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.Notifications, "Уведомления", "Новые записи и абонементы",
                        badgeCount = state.unreadNotifs, onClick = onNotificationsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.Person, "Профиль", "Настройки и данные",
                        onClick = onProfileClick)
                }
            }
        }
    }
}

private fun typeLabel(type: SubscriptionType) = when (type) {
    SubscriptionType.MONTHLY -> "Месячный"
    SubscriptionType.QUARTERLY -> "Квартальный"
    SubscriptionType.ANNUAL -> "Годовой"
}
