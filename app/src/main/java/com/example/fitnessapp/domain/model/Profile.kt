package com.example.fitnessapp.domain.model

data class Profile(
    val userId: String,
    val username: String,
    val role: String,
    val client: com.example.fitnessapp.domain.model.Client?
)
