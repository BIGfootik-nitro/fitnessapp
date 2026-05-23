package com.example.fitnessapp.domain.repository

import com.example.fitnessapp.domain.model.Profile

interface ProfileRepository {
    suspend fun getMe(): Result<Profile>
    suspend fun updateMyProfile(fullName: String, phone: String?, email: String?, birthDate: String?): Result<Unit>
}
