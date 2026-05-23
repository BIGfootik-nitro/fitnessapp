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
import com.example.fitnessapp.ui.theme.OnPrimaryContainer
import com.example.fitnessapp.ui.theme.Primary
import com.example.fitnessapp.ui.theme.PrimaryContainer
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
    var notifCount by remember { mutableStateOf(0) }

    LaunchedEffect(state) {
        if (state is ClientHomeUiState.Loaded) {
            // count unread notifications on first load (optional - skip for now)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фитнес-центр") },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Box {
                            Icon(Icons.Default.Notifications, null)
                        }
                    }
                }
            )
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

                    Text("Привет, $name!", fontSize = 28.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(20.dp))

                    // Active subscription card with gradient
                    if (state.activeSubs.isNotEmpty()) {
                        val sub = state.activeSubs.first()
                        GradientCard(modifier = Modifier.fillMaxWidth()) {
                            Text("АКТИВНЫЙ АБОНИМЕНТ", fontSize = 14.sp, fontWeight = FontWeight.W500,
                                color = OnPrimaryContainer)
                            Spacer(Modifier.height(4.dp))
                            Text(typeLabel(sub.type), fontSize = 22.sp, fontWeight = FontWeight.W600,
                                color = androidx.compose.ui.graphics.Color.White)
                            Spacer(Modifier.height(4.dp))
                            Text("Действует до ${sub.endDate}", fontSize = 14.sp, color = OnPrimaryContainer)
                        }
                    } else {
                        OutlinedCard(modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large) {
                            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(40.dp))
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

                    ActionCard(Icons.Default.EventAvailable, "Записаться на тренировку", "Выберите дату и время",
                        onClick = onBookClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.CardMembership, "Мои абонементы", "Просмотр и оформление",
                        onClick = onSubscriptionsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.History, "История посещений", "Ваши прошлые тренировки",
                        onClick = onVisitsClick)
                    Spacer(Modifier.height(8.dp))
                    ActionCard(Icons.Default.Notifications, "Уведомления", "Новые записи и абонементы",
                        badgeCount = notifCount, onClick = onNotificationsClick)
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
