package com.example.fitnessapp.domain.model

enum class BookingStatus { PENDING, CONFIRMED, CANCELLED }

data class Booking(
    val id: String,
    val clientId: String,
    val clientName: String?,
    val scheduledAt: String,
    val status: BookingStatus,
    val note: String?
)
