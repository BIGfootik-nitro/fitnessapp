package com.example.fitnessapp.domain.model

data class Visit(
    val id: String,
    val clientId: String,
    val visitedAt: String,
    val note: String?
)
