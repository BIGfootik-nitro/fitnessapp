package com.example.fitnessapp.domain.model

data class Client(
    val id: String,
    val fullName: String,
    val phone: String?,
    val email: String?,
    val birthDate: String?
)
