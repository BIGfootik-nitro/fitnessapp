package com.example.fitnessapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String)
