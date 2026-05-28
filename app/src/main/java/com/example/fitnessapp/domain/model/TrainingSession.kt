package com.example.fitnessapp.domain.model

data class TrainingSession(
    val id: String,
    val title: String,
    val description: String?,
    val scheduledAt: String,
    val durationMin: Int,
    val trainerName: String?,
    val maxCapacity: Int,
    val bookedCount: Int
) {
    val isFull get() = bookedCount >= maxCapacity
    val spotsLeft get() = maxCapacity - bookedCount
}

data class SessionAttendee(
    val bookingId: String,
    val clientId: String,
    val clientName: String,
    val status: String
)
