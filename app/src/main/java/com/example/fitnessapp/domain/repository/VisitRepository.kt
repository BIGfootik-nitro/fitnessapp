package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.Visit

interface VisitRepository {
    suspend fun getByClient(clientId: String): Result<List<Visit>>
    suspend fun add(clientId: String, visitedAt: String, note: String?): Result<String>
}
