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
                            Card(modifier = Modifier.fillMaxWidth()
                                .clickable { if (!notif.read) viewModel.markRead(notif.id) }) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(notif.title, fontWeight = if (notif.read) FontWeight.Normal else FontWeight.Bold)
                                        if (!notif.read) Text("new", color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold)
                                    }
                                    Text(notif.body)
                                    Text(notif.createdAt.substringBefore("T"),
                                        style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
