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
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.Booking
import com.example.fitnessapp.domain.model.BookingStatus
import com.example.fitnessapp.ui.components.*
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мои записи") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openCreate() },
                containerColor = MaterialTheme.colorScheme.primary) {
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
                            MyBookingCard(booking, onCancel = { viewModel.cancel(booking.id) })
                            Spacer(Modifier.height(12.dp))
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
private fun MyBookingCard(booking: Booking, onCancel: () -> Unit) {
    val variant = when (booking.status) {
        BookingStatus.PENDING -> BadgeVariant.PENDING
        BookingStatus.CONFIRMED -> BadgeVariant.CONFIRMED
        BookingStatus.CANCELLED -> BadgeVariant.CANCELLED
    }
    val badgeText = when (booking.status) {
        BookingStatus.PENDING -> "Ожидает"
        BookingStatus.CONFIRMED -> "Подтверждена"
        BookingStatus.CANCELLED -> "Отменена"
    }

    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.scheduledAt.substringBefore("T"), fontSize = 16.sp, fontWeight = FontWeight.W600)
                StatusBadge(badgeText, variant)
            }
            booking.note?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (booking.status == BookingStatus.PENDING) {
                Spacer(Modifier.height(12.dp))
                FitnessButton("Отменить", onClick = onCancel, outlined = true, destructive = true)
            }
        }
    }
}

@Composable
private fun CreateBookingDialog(
    onCreate: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("10:00") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = { Text("Записаться на тренировку", fontWeight = FontWeight.W500) },
        text = {
            Column {
                DatePickerField("Дата", dateText, { dateText = it }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                TimePickerField("Время", timeText, { timeText = it }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = noteText, onValueChange = { noteText = it },
                    label = { Text("Комментарий (необязательно)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
            }
        },
        confirmButton = {
            FitnessButton("Записаться", onClick = {
                val dt = LocalDateTime.parse("${dateText}T${timeText}:00")
                onCreate(dt.toInstant(ZoneOffset.UTC).toString(), noteText.ifBlank { null })
            }, enabled = dateText.isNotBlank() && timeText.isNotBlank())
        },
        dismissButton = { FitnessButton("Отмена", onClick = onDismiss, outlined = true) }
    )
}
