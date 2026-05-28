package com.example.fitnessapp.presentation.clients.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.domain.model.Client
import com.example.fitnessapp.ui.components.AvatarInitials
import com.example.fitnessapp.ui.components.FitnessButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
    onClientClick: (String) -> Unit,
    onAddClient: () -> Unit,
    onLogout: () -> Unit,
    onBookings: () -> Unit,
    onSessions: () -> Unit,
    viewModel: ClientListViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(state) {
        if (state is ClientListUiState.Unauthorized) onLogout()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Клиенты") },
                actions = {
                    IconButton(onClick = onSessions) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = "Тренировки")
                    }
                    IconButton(onClick = onBookings) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Записи")
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Выйти")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClient,
                containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по имени") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            when (state) {
                is ClientListUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                is ClientListUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        FitnessButton("Повторить", onClick = { viewModel.load() })
                    }
                }
                is ClientListUiState.Success -> {
                    if (state.clients.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Клиентов пока нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.clients, key = { it.id }) { client ->
                                ClientItem(client, onClick = { onClientClick(client.id) })
                                HorizontalDivider()
                            }
                        }
                    }
                }
                ClientListUiState.Unauthorized -> {}
            }
        }
    }
}

@Composable
private fun ClientItem(client: Client, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarInitials(client.fullName, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(client.fullName, fontWeight = FontWeight.W500, fontSize = 16.sp)
            val sub = listOfNotNull(client.phone, client.email).joinToString(" · ")
            if (sub.isNotEmpty()) Text(sub, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
