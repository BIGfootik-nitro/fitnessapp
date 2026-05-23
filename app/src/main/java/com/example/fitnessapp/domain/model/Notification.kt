package com.example.fitnessapp.domain.model

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val read: Boolean,
    val createdAt: String
)
