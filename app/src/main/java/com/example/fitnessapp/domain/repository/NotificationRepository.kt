package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.Notification

interface NotificationRepository {
    suspend fun getMine(): Result<List<Notification>>
    suspend fun markRead(id: String): Result<Unit>
}
