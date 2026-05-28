package com.example.fitnessapp.presentation.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.domain.model.TrainingSession
import com.example.fitnessapp.ui.components.FitnessButton
import com.example.fitnessapp.ui.components.StatusBadge
import com.example.fitnessapp.ui.components.BadgeVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionListScreen(
    onSessionClick: (String) -> Unit,
    onCreateSession: () -> Unit,
    onBack: () -> Unit,
    isAdmin: Boolean = false,
    viewModel: SessionListViewModel = viewModel()
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Тренировки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.People, null)
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateSession,
                containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, "Создать тренировку")
            }
        }
    ) { padding ->
        when (state) {
            is SessionListUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                Alignment.Center) { CircularProgressIndicator() }
            is SessionListUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    FitnessButton("Повторить", onClick = { viewModel.load() })
                }
            }
            is SessionListUiState.Loaded -> {
                if (state.sessions.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                        Text("Нет тренировок. Создайте первую!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                        items(state.sessions, key = { it.id }) { session ->
                            SessionItem(session, onClick = { onSessionClick(session.id) })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionItem(session: TrainingSession, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)) {
            Box(contentAlignment = Alignment.Center) {
                val day = session.scheduledAt.substring(8, 10)
                Text(day, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(session.title, fontWeight = FontWeight.W600, fontSize = 16.sp)
            Text("${session.scheduledAt.substringBefore('T')} · ${session.durationMin} мин" +
                (session.trainerName?.let { " · $it" } ?: ""),
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text("${session.bookedCount}/${session.maxCapacity}",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (session.isFull) StatusBadge("Мест нет", BadgeVariant.CANCELLED)
        }
    }
}
