package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.Client

interface ClientRepository {
    suspend fun getAll(search: String = ""): Result<List<Client>>
    suspend fun getById(id: String): Result<Client>
    suspend fun create(fullName: String, phone: String?, email: String?, birthDate: String?): Result<String>
    suspend fun update(id: String, fullName: String, phone: String?, email: String?, birthDate: String?): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
