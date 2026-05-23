package com.example.fitnessapp.presentation.client.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.Notification
import com.example.fitnessapp.ui.components.StatusBadge
import com.example.fitnessapp.ui.components.BadgeVariant
import com.example.fitnessapp.ui.theme.Primary
import com.example.fitnessapp.ui.theme.PrimaryContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNotificationsScreen(
    viewModel: MyNotificationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    Scaffold(topBar = { TopAppBar(title = { Text("Уведомления") }) }) { padding ->
        when (state) {
            is MyNotifsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is MyNotifsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is MyNotifsUiState.Loaded -> {
                if (state.items.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center) { Text("Нет уведомлений") }
                } else {
                    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                        .verticalScroll(rememberScrollState())) {
                        state.items.forEach { notif ->
                            NotificationCard(notif, onMarkRead = { viewModel.markRead(notif.id) })
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notif: Notification, onMarkRead: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!notif.read) onMarkRead() },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = MaterialTheme.shapes.medium, color = PrimaryContainer,
                modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(notif.title.take(1), fontSize = 16.sp, fontWeight = FontWeight.W600, color = Primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(notif.title, fontWeight = if (notif.read) FontWeight.Normal else FontWeight.Bold)
                    if (!notif.read) {
                        Surface(shape = MaterialTheme.shapes.extraSmall, color = Primary) {
                            Text("new", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp, fontWeight = FontWeight.W600, color = androidx.compose.ui.graphics.Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(notif.body, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(notif.createdAt.substringBefore("T"), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
