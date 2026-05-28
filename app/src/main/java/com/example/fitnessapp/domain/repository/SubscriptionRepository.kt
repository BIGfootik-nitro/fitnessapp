package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.Subscription

interface SubscriptionRepository {
    suspend fun getByClient(clientId: String): Result<List<Subscription>>
    suspend fun getMine(): Result<List<Subscription>>
    suspend fun create(clientId: String, type: String, startDate: String, endDate: String, price: String): Result<String>
    suspend fun buyMine(type: String, startDate: String, endDate: String, price: String): Result<Unit>
    suspend fun toggleFreeze(subscriptionId: String): Result<Boolean>
    suspend fun updateSub(id: String, type: String, startDate: String, endDate: String, price: String): Result<Unit>
    suspend fun deleteSub(id: String): Result<Unit>
}
