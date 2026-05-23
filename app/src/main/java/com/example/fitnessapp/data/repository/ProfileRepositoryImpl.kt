package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.dto.ClientRequest
import com.example.fitnessapp.data.remote.dto.MeResponse
import com.example.fitnessapp.domain.model.Client
import com.example.fitnessapp.domain.model.Profile
import com.example.fitnessapp.domain.repository.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class ProfileRepositoryImpl(private val client: HttpClient) : ProfileRepository {

    override suspend fun getMe(): Result<Profile> = runCatching {
        val response = client.get("/me")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        val me: MeResponse = response.body()
        Profile(
            userId = me.userId,
            username = me.username,
            role = me.role,
            client = me.client?.let {
                Client(it.id, it.fullName, it.phone, it.email, it.birthDate)
            }
        )
    }

    override suspend fun updateMyProfile(fullName: String, phone: String?, email: String?, birthDate: String?): Result<Unit> = runCatching {
        val response = client.put("/me/profile") {
            setBody(ClientRequest(fullName, phone, email, birthDate))
        }
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось обновить профиль")
    }
}
