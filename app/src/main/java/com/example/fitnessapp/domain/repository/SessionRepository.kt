package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.SessionAttendee
import com.example.fitnessapp.domain.model.TrainingSession

interface SessionRepository {
    suspend fun getAll(): Result<List<TrainingSession>>
    suspend fun getById(id: String): Result<TrainingSession>
    suspend fun create(title: String, description: String?, scheduledAt: String, durationMin: Int, maxCapacity: Int): Result<String>
    suspend fun update(id: String, title: String, description: String?, scheduledAt: String, durationMin: Int, maxCapacity: Int): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    suspend fun book(sessionId: String): Result<Unit>
    suspend fun getAttendees(sessionId: String): Result<List<SessionAttendee>>
    suspend fun markAttended(sessionId: String, bookingId: String): Result<Unit>
}
