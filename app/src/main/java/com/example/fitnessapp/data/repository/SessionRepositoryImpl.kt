package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.remote.dto.SessionAttendeeResponse
import com.example.fitnessapp.data.remote.dto.SessionRequest
import com.example.fitnessapp.data.remote.dto.SessionResponse
import com.example.fitnessapp.domain.model.SessionAttendee
import com.example.fitnessapp.domain.model.TrainingSession
import com.example.fitnessapp.domain.repository.SessionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

class SessionRepositoryImpl(private val client: HttpClient) : SessionRepository {

    override suspend fun getAll(): Result<List<TrainingSession>> = runCatching {
        val r = client.get("/sessions")
        ensureSuccess(r.status)
        r.body<List<SessionResponse>>().map { it.toDomain() }
    }

    override suspend fun getById(id: String): Result<TrainingSession> = runCatching {
        val r = client.get("/sessions/$id")
        ensureSuccess(r.status)
        r.body<SessionResponse>().toDomain()
    }

    override suspend fun create(title: String, description: String?, scheduledAt: String,
                                durationMin: Int, maxCapacity: Int): Result<String> = runCatching {
        val r = client.post("/sessions") {
            setBody(SessionRequest(title, description, scheduledAt, durationMin, maxCapacity))
        }
        ensureSuccess(r.status)
        r.body<Map<String, String>>()["id"] ?: ""
    }

    override suspend fun update(id: String, title: String, description: String?, scheduledAt: String,
                                durationMin: Int, maxCapacity: Int): Result<Unit> = runCatching {
        val r = client.put("/sessions/$id") {
            setBody(SessionRequest(title, description, scheduledAt, durationMin, maxCapacity))
        }
        ensureSuccess(r.status)
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        val r = client.delete("/sessions/$id")
        ensureSuccess(r.status)
    }

    override suspend fun book(sessionId: String): Result<Unit> = runCatching {
        val r = client.post("/sessions/$sessionId/book")
        ensureSuccess(r.status)
    }

    override suspend fun getAttendees(sessionId: String): Result<List<SessionAttendee>> = runCatching {
        val r = client.get("/sessions/$sessionId/attendees")
        ensureSuccess(r.status)
        r.body<List<SessionAttendeeResponse>>().map {
            SessionAttendee(it.bookingId, it.clientId, it.clientName, it.status)
        }
    }

    override suspend fun markAttended(sessionId: String, bookingId: String): Result<Unit> = runCatching {
        val r = client.post("/sessions/$sessionId/attend/$bookingId")
        ensureSuccess(r.status)
    }

    private fun ensureSuccess(status: HttpStatusCode) {
        when {
            status == HttpStatusCode.Unauthorized -> throw UnauthorizedException()
            status == HttpStatusCode.Forbidden -> throw RuntimeException("Нет доступа")
            status == HttpStatusCode.Conflict -> throw RuntimeException("Уже записан или нет мест")
            !status.isSuccess() -> throw RuntimeException("Ошибка: ${status.value}")
        }
    }

    private fun SessionResponse.toDomain() = TrainingSession(
        id, title, description, scheduledAt, durationMin, trainerName, maxCapacity, bookedCount
    )
}
