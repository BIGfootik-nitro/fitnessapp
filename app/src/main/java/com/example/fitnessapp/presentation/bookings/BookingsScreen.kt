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
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.Booking
import com.example.fitnessapp.domain.model.BookingStatus
import com.example.fitnessapp.ui.components.*

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
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainerBookingCard(booking: Booking, onConfirm: () -> Unit, onCancel: () -> Unit) {
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
    val isPending = booking.status == BookingStatus.PENDING

    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.clientName ?: "Клиент", fontSize = 16.sp, fontWeight = FontWeight.W600)
                StatusBadge(badgeText, variant)
            }
            Spacer(Modifier.height(4.dp))
            Text("Дата: ${booking.scheduledAt.substringBefore("T")}", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            booking.note?.let {
                Text("Комментарий: $it", fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isPending) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FitnessButton("Отклонить", onClick = onCancel, outlined = true, destructive = true)
                    FitnessButton("Подтвердить", onClick = onConfirm)
                }
            }
        }
    }
}
