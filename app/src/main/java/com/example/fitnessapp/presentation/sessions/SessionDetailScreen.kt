package com.example.fitnessapp.presentation.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.domain.model.SessionAttendee
import com.example.fitnessapp.ui.components.AvatarInitials
import androidx.compose.material3.LocalContentColor
import com.example.fitnessapp.ui.components.FitnessButton
import com.example.fitnessapp.ui.components.GradientCard
import com.example.fitnessapp.ui.components.StatusBadge
import com.example.fitnessapp.ui.components.BadgeVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    sessionId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    isAdmin: Boolean = false,
    viewModel: SessionDetailViewModel = viewModel()
) {
    LaunchedEffect(sessionId) { viewModel.load(sessionId) }
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Тренировка") },
                navigationIcon = { IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                } },
                actions = {
                    IconButton(onClick = { onEdit(sessionId) }) {
                        Icon(Icons.Default.Edit, "Редактировать")
                    }
                }
            )
        }
    ) { padding ->
        when (state) {
            is SessionDetailUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                Alignment.Center) { CircularProgressIndicator() }
            is SessionDetailUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    FitnessButton("Повторить", onClick = { viewModel.load(sessionId) })
                }
            }
            is SessionDetailUiState.Loaded -> {
                val session = state.session
                LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                    item {
                        // Info card
                        GradientCard(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(session.title, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                color = LocalContentColor.current)
                            Spacer(Modifier.height(4.dp))
                            Text("${session.scheduledAt.substringBefore('T')} · ${session.scheduledAt.substring(11,16)}",
                                fontSize = 14.sp, color = LocalContentColor.current.copy(alpha = 0.8f))
                            Text("${session.durationMin} мин · ${session.bookedCount}/${session.maxCapacity} участников",
                                fontSize = 14.sp, color = LocalContentColor.current.copy(alpha = 0.8f))
                            session.trainerName?.let {
                                Text("Тренер: $it", fontSize = 14.sp, color = LocalContentColor.current.copy(alpha = 0.8f))
                            }
                            session.description?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(it, fontSize = 14.sp, color = LocalContentColor.current.copy(alpha = 0.8f))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Участники (${state.attendees.size})",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontWeight = FontWeight.W600, fontSize = 18.sp)
                        Spacer(Modifier.height(4.dp))
                    }
                    if (state.attendees.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                                Text("Записей нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(state.attendees, key = { it.bookingId }) { attendee ->
                            AttendeeItem(attendee, onMarkAttended = {
                                viewModel.markAttended(sessionId, attendee.bookingId)
                            })
                            HorizontalDivider()
                        }
                    }
                    if (isAdmin) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            FitnessButton("Удалить тренировку",
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                onClick = { viewModel.delete(sessionId) { onBack() } },
                                outlined = true, destructive = true)
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendeeItem(attendee: SessionAttendee, onMarkAttended: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarInitials(attendee.clientName, modifier = Modifier.size(40.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(attendee.clientName, fontWeight = FontWeight.W500)
            val variant = when (attendee.status) {
                "CONFIRMED" -> BadgeVariant.CONFIRMED
                "CANCELLED" -> BadgeVariant.CANCELLED
                else -> BadgeVariant.PENDING
            }
            val label = when (attendee.status) {
                "CONFIRMED" -> "Подтверждён"
                "CANCELLED" -> "Отменён"
                else -> "Ожидает"
            }
            StatusBadge(label, variant)
        }
        if (attendee.status == "PENDING") {
            IconButton(onClick = onMarkAttended) {
                Icon(Icons.Default.CheckCircle, "Отметить присутствие",
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
