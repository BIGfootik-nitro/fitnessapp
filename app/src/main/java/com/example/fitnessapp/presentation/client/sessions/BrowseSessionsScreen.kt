package com.example.fitnessapp.presentation.client.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.fitnessapp.ui.components.GradientCard
import com.example.fitnessapp.ui.components.StatusBadge
import com.example.fitnessapp.ui.components.BadgeVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSessionsScreen(
    onBack: () -> Unit,
    viewModel: BrowseSessionsViewModel = viewModel()
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Записаться на тренировку") },
                navigationIcon = { IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                } })
        }
    ) { padding ->
        when (state) {
            is BrowseSessionsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                Alignment.Center) { CircularProgressIndicator() }
            is BrowseSessionsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    FitnessButton("Повторить", onClick = { viewModel.load() })
                }
            }
            is BrowseSessionsUiState.Loaded -> {
                if (state.sessions.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                        Text("Нет доступных тренировок", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                        item { Spacer(Modifier.height(8.dp)) }
                        items(state.sessions, key = { it.id }) { session ->
                            SessionCard(
                                session = session,
                                isBooking = viewModel.bookingInProgress == session.id,
                                onBook = { viewModel.book(session.id) }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    state.let {
        if (it is BrowseSessionsUiState.Loaded && it.successMessage != null) {
            LaunchedEffect(it.successMessage) {
                kotlinx.coroutines.delay(2000)
                viewModel.clearSuccess()
            }
            Snackbar(modifier = Modifier.padding(16.dp)) {
                Text(it.successMessage)
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: TrainingSession,
    isBooking: Boolean,
    onBook: () -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(session.title, fontWeight = FontWeight.W600, fontSize = 17.sp,
                    modifier = Modifier.weight(1f))
                if (session.isFull) StatusBadge("Мест нет", BadgeVariant.CANCELLED)
                else StatusBadge("${session.spotsLeft} мест", BadgeVariant.CONFIRMED)
            }
            Spacer(Modifier.height(4.dp))
            Text("${session.scheduledAt.substringBefore('T')} в ${session.scheduledAt.substring(11,16)}",
                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${session.durationMin} мин" + (session.trainerName?.let { " · $it" } ?: ""),
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            session.description?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            FitnessButton(
                text = if (isBooking) "Запись..." else "Записаться",
                onClick = onBook,
                enabled = !session.isFull && !isBooking,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
