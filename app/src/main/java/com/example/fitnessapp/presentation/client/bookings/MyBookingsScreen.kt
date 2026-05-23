package com.example.fitnessapp.presentation.client.bookings

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
import com.example.fitnessapp.domain.model.Booking
import com.example.fitnessapp.domain.model.BookingStatus
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мои записи") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openCreate() }) {
                Icon(Icons.Default.Add, "Записаться")
            }
        }
    ) { padding ->
        when (state) {
            is MyBookingsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is MyBookingsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is MyBookingsUiState.Loaded -> {
                if (state.bookings.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center) { Text("Нет записей") }
                } else {
                    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                        .verticalScroll(rememberScrollState())) {
                        state.bookings.forEach { booking ->
                            BookingCard(booking, onCancel = { viewModel.cancel(booking.id) })
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showCreateDialog) {
        CreateBookingDialog(
            onCreate = { iso, note -> viewModel.create(iso, note) },
            onDismiss = { viewModel.closeCreate() }
        )
    }
}

@Composable
private fun BookingCard(booking: Booking, onCancel: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.scheduledAt.substringBefore("T"), fontWeight = FontWeight.SemiBold)
                StatusChip(booking.status)
            }
            booking.note?.let { Text(it) }
            if (booking.status == BookingStatus.PENDING) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onCancel) { Text("Отменить") }
            }
        }
    }
}

@Composable
private fun StatusChip(status: BookingStatus) {
    val (text, color) = when (status) {
        BookingStatus.PENDING -> "Ожидает" to MaterialTheme.colorScheme.tertiary
        BookingStatus.CONFIRMED -> "Подтверждена" to MaterialTheme.colorScheme.primary
        BookingStatus.CANCELLED -> "Отменена" to MaterialTheme.colorScheme.error
    }
    Text(text, color = color, fontWeight = FontWeight.Medium)
}

@Composable
private fun CreateBookingDialog(
    onCreate: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("10:00") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Записаться на тренировку") },
        text = {
            Column {
                OutlinedTextField(value = dateText, onValueChange = { dateText = it },
                    label = { Text("Дата (ГГГГ-ММ-ДД)") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = timeText, onValueChange = { timeText = it },
                    label = { Text("Время (ЧЧ:ММ)") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = noteText, onValueChange = { noteText = it },
                    label = { Text("Комментарий (необязательно)") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dt = LocalDateTime.parse("${dateText}T$timeText:00")
                onCreate(dt.toInstant(ZoneOffset.UTC).toString(), noteText.ifBlank { null })
            }, enabled = dateText.isNotBlank() && timeText.isNotBlank()) { Text("Записаться") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
