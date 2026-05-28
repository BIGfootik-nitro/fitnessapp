package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.dto.NotificationResponse
import com.example.fitnessapp.domain.model.Notification
import com.example.fitnessapp.domain.repository.NotificationRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class NotificationRepositoryImpl(private val client: HttpClient) : NotificationRepository {

    override suspend fun getMine(): Result<List<Notification>> = runCatching {
        val response = client.get("/me/notifications")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Ошибка сервера: ${response.status.value}")
        val list: List<NotificationResponse> = response.body()
        list.map { Notification(it.id, it.title, it.body, it.read, it.createdAt) }
    }

    override suspend fun markRead(id: String): Result<Unit> = runCatching {
        val response = client.patch("/me/notifications/$id/read")
        if (response.status == HttpStatusCode.Unauthorized) throw UnauthorizedException()
        if (!response.status.isSuccess()) throw RuntimeException("Не удалось отметить")
    }
}
