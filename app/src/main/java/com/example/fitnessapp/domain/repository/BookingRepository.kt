package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.Booking

interface BookingRepository {
    suspend fun getMine(): Result<List<Booking>>
    suspend fun getAll(): Result<List<Booking>>
    suspend fun create(scheduledAtIso: String, note: String?): Result<Unit>
    suspend fun cancelMine(id: String): Result<Unit>
    suspend fun changeStatus(id: String, status: String): Result<Unit>
}
