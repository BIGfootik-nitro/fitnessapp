package com.example.fitnessapp.presentation.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.domain.model.Booking
import com.example.fitnessapp.domain.model.BookingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    viewModel: BookingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = viewModel.uiState

    Scaffold(topBar = { TopAppBar(title = { Text("Записи клиентов") }) }) { padding ->
        when (state) {
            is BookingsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is BookingsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            is BookingsUiState.Loaded -> {
                if (state.bookings.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center) { Text("Нет записей") }
                } else {
                    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
                        .verticalScroll(rememberScrollState())) {
                        state.bookings.forEach { booking ->
                            TrainerBookingCard(
                                booking = booking,
                                onConfirm = { viewModel.confirm(booking.id) },
                                onCancel = { viewModel.cancel(booking.id) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainerBookingCard(booking: Booking, onConfirm: () -> Unit, onCancel: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(booking.clientName ?: "Клиент", fontWeight = FontWeight.SemiBold)
            Text("Дата: ${booking.scheduledAt.substringBefore("T")}")
            booking.note?.let { Text("Комментарий: $it") }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                StatusChip(booking.status)
                if (booking.status == BookingStatus.PENDING) {
                    Row {
                        OutlinedButton(onClick = onCancel) { Text("Отклонить") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = onConfirm) { Text("Подтвердить") }
                    }
                }
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
