package com.example.fitnessapp.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun register(username: String, password: String, role: String = "TRAINER"): Result<Unit>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getRole(): String?
}
